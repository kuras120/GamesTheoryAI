package com.games.theory.tictactoe.integration;

import com.games.theory.utils.ApplicationDirectories;
import com.games.theory.utils.CommandResult;
import com.games.theory.utils.CommandRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PythonRuntimeManagerTest {
    private static final String WHEEL_FILENAME = "games_theory-0.0.3-py3-none-any.whl";

    @TempDir
    Path temporaryDirectory;

    @Test
    void parsesAndValidatesPythonVersions() {
        assertEquals(
            Optional.of(new PythonInterpreterDiscovery.PythonVersion(3, 9)),
            PythonInterpreterDiscovery.parseVersion("Python 3.9.18")
        );
        assertTrue(PythonInterpreterDiscovery.parseVersion("Python 3.14.0rc1").orElseThrow().supported());
        assertFalse(PythonInterpreterDiscovery.parseVersion("Python 3.8.20").orElseThrow().supported());
        assertTrue(PythonInterpreterDiscovery.parseVersion("invalid").isEmpty());
    }

    @Test
    void preservesWindowsLauncherPrefix() {
        assertEquals(
            List.of(List.of("py", "-3"), List.of("python3"), List.of("python")),
            PythonInterpreterDiscovery.candidates("Windows 11")
        );
    }

    @Test
    void resolvesWindowsVirtualEnvironmentEntryPoints() {
        ApplicationDirectories directories = directories();
        PythonEnvironmentManager manager = new PythonEnvironmentManager(
            directories,
            successfulRunner(),
            "Windows 11"
        );

        PythonRuntime runtime = manager.runtimePaths(temporaryDirectory.resolve("venv"));

        assertEquals(temporaryDirectory.resolve("venv/Scripts/python.exe"), runtime.pythonExecutable());
        assertEquals(temporaryDirectory.resolve("venv/Scripts/games-theory.exe"), runtime.gamesTheoryExecutable());
        assertEquals(
            temporaryDirectory.resolve("venv/Scripts/games-theory-init.exe"),
            runtime.gamesTheoryInitExecutable()
        );
    }

    @Test
    void reportsMissingPythonWithoutCreatingRuntime() {
        FakeRunner runner = new FakeRunner(command -> {
            throw new IOException("not found");
        });

        PythonRuntimeResult result = manager(runner, resources("wheel-content")).prepare();

        assertFalse(result.isAvailable());
        assertEquals(
            "AI unavailable. Install Python 3.9 or newer and restart the application.",
            result.statusMessage()
        );
        assertFalse(Files.exists(temporaryDirectory.resolve("runtime")));
    }

    @Test
    void reportsUnsupportedPythonVersion() {
        FakeRunner runner = new FakeRunner(command -> new CommandResult(0, "Python 3.8.20", ""));

        PythonRuntimeResult result = manager(runner, resources("wheel-content")).prepare();

        assertFalse(result.isAvailable());
        assertEquals(
            "AI unavailable. Detected Python 3.8; Python 3.9 or newer is required.",
            result.statusMessage()
        );
    }

    @Test
    void reportsMissingVenvSupportSeparately() {
        FakeRunner runner = new FakeRunner(command -> {
            if (command.getLast().equals("--version")) {
                return new CommandResult(0, "Python 3.11.0", "");
            }
            return new CommandResult(1, "", "ensurepip is not available");
        });

        PythonRuntimeResult result = manager(runner, resources("wheel-content")).prepare();

        assertFalse(result.isAvailable());
        assertEquals(
            "AI unavailable. Python virtual environment support is missing. Install it and restart the application.",
            result.statusMessage()
        );
    }

    @Test
    void installsOnlyFromExtractedWheelhouse() {
        FakeRunner runner = runnerCreatingRuntimeFiles();

        PythonRuntimeResult result = manager(runner, resources("wheel-content")).prepare();

        assertTrue(result.isAvailable());
        PythonRuntime runtime = result.runtime();
        assertEquals(temporaryDirectory.resolve("runtime/venv/bin/python"), runtime.pythonExecutable());
        assertEquals(temporaryDirectory, runtime.applicationDataDirectory());
        List<String> install = runner.commands.stream()
            .filter(command -> command.contains("pip"))
            .findFirst()
            .orElseThrow();
        assertTrue(install.contains("--no-index"));
        assertTrue(install.contains("--find-links"));
        assertTrue(install.contains(temporaryDirectory.resolve("runtime/wheelhouse").toString()));
        assertTrue(install.contains("games-theory==0.0.3"));
        assertFalse(install.stream().anyMatch(argument -> argument.startsWith("http")));
        assertTrue(Files.exists(temporaryDirectory.resolve("runtime/installed-wheelhouse.sha256")));
    }

    @Test
    void rejectsWheelWithUnexpectedHash() {
        FakeResources resources = resources("wheel-content");
        resources.content.put(
            WheelhouseInstaller.WHEELHOUSE_RESOURCE + "/" + WheelhouseInstaller.MANIFEST_FILENAME,
            manifest("0".repeat(64))
        );

        PythonRuntimeResult result = manager(successfulRunner(), resources).prepare();

        assertFalse(result.isAvailable());
        assertEquals("AI unavailable. Setup failed. Restart the application.", result.statusMessage());
    }

    @Test
    void reusesMatchingRuntimeWithoutReinstallingWheelhouse() {
        FakeResources resources = resources("wheel-v1");
        PythonRuntimeResult initial = manager(runnerCreatingRuntimeFiles(), resources).prepare();
        assertTrue(initial.isAvailable());

        FakeRunner secondRun = successfulRunner();
        PythonRuntimeResult reused = manager(secondRun, resources).prepare();

        assertTrue(reused.isAvailable());
        assertFalse(secondRun.commands.stream().anyMatch(command -> command.contains("pip")));
        assertFalse(secondRun.commands.stream().anyMatch(command -> command.contains("venv")));
        assertTrue(secondRun.commands.stream().anyMatch(command ->
            command.getFirst().endsWith("games-theory-init")));
    }

    @Test
    void reinstallsChangedWheelhouseWithoutDeletingLearningData() throws Exception {
        PythonRuntimeResult initial = manager(runnerCreatingRuntimeFiles(), resources("wheel-v1")).prepare();
        assertTrue(initial.isAvailable());
        Path qtable = temporaryDirectory.resolve("data/qtable.json");
        Files.createDirectories(qtable.getParent());
        Files.writeString(qtable, "learned-data");

        FakeRunner secondRun = successfulRunner();
        PythonRuntimeResult upgraded = manager(secondRun, resources("wheel-v2")).prepare();

        assertTrue(upgraded.isAvailable());
        assertTrue(secondRun.commands.stream().anyMatch(command -> command.contains("pip")));
        assertFalse(secondRun.commands.stream().anyMatch(command -> command.contains("venv")));
        assertEquals("learned-data", Files.readString(qtable));
    }

    private PythonRuntimeManager manager(FakeRunner runner, FakeResources resources) {
        return manager(runner, resources, "Linux");
    }

    private PythonRuntimeManager manager(FakeRunner runner, FakeResources resources, String osName) {
        return new PythonRuntimeManager(directories(), runner, resources, osName);
    }

    private ApplicationDirectories directories() {
        return new ApplicationDirectories(
            temporaryDirectory,
            temporaryDirectory.resolve("runtime"),
            temporaryDirectory.resolve("data")
        );
    }

    private FakeResources resources(String wheelContent) {
        String hash = sha256(wheelContent);
        return new FakeResources(new java.util.HashMap<>(Map.of(
            WheelhouseInstaller.WHEELHOUSE_RESOURCE + "/" + WheelhouseInstaller.MANIFEST_FILENAME,
            manifest(hash),
            WheelhouseInstaller.WHEELHOUSE_RESOURCE + "/" + WHEEL_FILENAME,
            wheelContent
        )));
    }

    private static String manifest(String hash) {
        return "requirement\tgames-theory==0.0.3\n"
            + "wheel\t" + hash + "\t" + WHEEL_FILENAME + "\n";
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private FakeRunner successfulRunner() {
        return new FakeRunner(command -> {
            if (command.getLast().equals("--version")) {
                return new CommandResult(0, "Python 3.14.0", "");
            }
            return new CommandResult(0, "", "");
        });
    }

    private FakeRunner runnerCreatingRuntimeFiles() {
        return new FakeRunner(command -> {
            if (command.getLast().equals("--version")) {
                return new CommandResult(0, "Python 3.14.0", "");
            }
            Path scripts = temporaryDirectory.resolve("runtime/venv/bin");
            if (command.contains("venv")) {
                Files.createDirectories(scripts);
                Files.createFile(scripts.resolve("python"));
            }
            if (command.contains("pip")) {
                Files.createFile(scripts.resolve("games-theory"));
                Files.createFile(scripts.resolve("games-theory-init"));
            }
            return new CommandResult(0, "", "");
        });
    }

    @FunctionalInterface
    private interface CommandBehavior {
        CommandResult run(List<String> command) throws IOException, InterruptedException, TimeoutException;
    }

    private static class FakeRunner implements CommandRunner {
        private final CommandBehavior behavior;
        private final List<List<String>> commands = new ArrayList<>();

        private FakeRunner(CommandBehavior behavior) {
            this.behavior = behavior;
        }

        @Override
        public CommandResult run(List<String> command, Duration timeout)
            throws IOException, InterruptedException, TimeoutException {
            commands.add(List.copyOf(command));
            return behavior.run(command);
        }
    }

    private static class FakeResources implements RuntimeResources {
        private final Map<String, String> content;

        private FakeResources(Map<String, String> content) {
            this.content = content;
        }

        @Override
        public String read(String resource) throws IOException {
            String value = content.get(resource);
            if (value == null) {
                throw new IOException("Missing resource: " + resource);
            }
            return value;
        }

        @Override
        public void copy(String resource, Path destination) throws IOException {
            Files.createDirectories(destination.getParent());
            Files.writeString(destination, read(resource));
        }
    }
}
