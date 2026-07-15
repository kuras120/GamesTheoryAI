package com.games.theory.tictactoe.integration;

import com.games.theory.utils.ApplicationDirectories;
import com.games.theory.utils.CommandResult;
import com.games.theory.utils.CommandRunner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

@Slf4j
final class PythonEnvironmentManager {
    private static final Duration DISCOVERY_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration SETUP_TIMEOUT = Duration.ofMinutes(2);

    private final ApplicationDirectories directories;
    private final CommandRunner commandRunner;
    private final String osName;

    PythonEnvironmentManager(ApplicationDirectories directories, CommandRunner commandRunner, String osName) {
        this.directories = directories;
        this.commandRunner = commandRunner;
        this.osName = osName;
    }

    PreparedPythonRuntime prepare(List<String> pythonCommand)
        throws IOException, InterruptedException, TimeoutException, VenvUnavailableException {
        Path venv = directories.runtime().resolve("venv");
        PythonRuntime runtime = runtimePaths(venv);
        boolean reusable = isReusable(runtime);
        if (!reusable) {
            RuntimeFileOperations.deleteDirectory(venv);
            createVenv(pythonCommand, venv);
        }
        return new PreparedPythonRuntime(runtime, reusable);
    }

    void initialize(PythonRuntime runtime) throws IOException, InterruptedException, TimeoutException {
        runRequired(List.of(
            runtime.gamesTheoryInitExecutable().toString(),
            directories.root().toString()
        ));
    }

    PythonRuntime runtimePaths(Path venv) {
        boolean windows = osName.toLowerCase(Locale.ROOT).contains("win");
        Path scripts = venv.resolve(windows ? "Scripts" : "bin");
        String suffix = windows ? ".exe" : "";
        return new PythonRuntime(
            scripts.resolve("python" + suffix),
            scripts.resolve("games-theory" + suffix),
            scripts.resolve("games-theory-init" + suffix),
            directories.root()
        );
    }

    private void createVenv(List<String> pythonCommand, Path venv)
        throws IOException, InterruptedException, TimeoutException, VenvUnavailableException {
        CommandResult result = commandRunner.run(append(pythonCommand, "-m", "venv", venv.toString()), SETUP_TIMEOUT);
        logDiagnostics(result, pythonCommand.getFirst());
        if (result.successful()) {
            return;
        }
        String diagnostics = (result.stdout() + "\n" + result.stderr()).toLowerCase(Locale.ROOT);
        if (diagnostics.contains("no module named venv")
            || diagnostics.contains("ensurepip is not available")
            || diagnostics.contains("install the python3-venv")) {
            throw new VenvUnavailableException("Python venv support is unavailable");
        }
        throw new IOException("Unable to create Python virtual environment: " + result.stderr());
    }

    private boolean isReusable(PythonRuntime runtime) throws InterruptedException {
        if (!Files.exists(runtime.pythonExecutable())
            || !Files.exists(runtime.gamesTheoryExecutable())
            || !Files.exists(runtime.gamesTheoryInitExecutable())) {
            return false;
        }
        try {
            CommandResult result = commandRunner.run(
                List.of(runtime.pythonExecutable().toString(), "--version"),
                DISCOVERY_TIMEOUT
            );
            return result.successful()
                && PythonInterpreterDiscovery.parseVersion(result.stdout() + "\n" + result.stderr())
                    .map(PythonInterpreterDiscovery.PythonVersion::supported)
                    .orElse(false);
        } catch (IOException | TimeoutException exception) {
            log.warn("Existing Python runtime cannot be reused", exception);
            return false;
        }
    }

    private void runRequired(List<String> command) throws IOException, InterruptedException, TimeoutException {
        CommandResult result = commandRunner.run(command, SETUP_TIMEOUT);
        logDiagnostics(result, command.getFirst());
        if (!result.successful()) {
            throw new IOException("Command failed with exit code " + result.exitCode() + ": " + command.getFirst());
        }
    }

    private static void logDiagnostics(CommandResult result, String executable) {
        if (!result.stderr().isBlank()) {
            log.debug("Command diagnostic output for {}:\n{}", executable, result.stderr());
        }
    }

    private static List<String> append(List<String> prefix, String... arguments) {
        List<String> command = new ArrayList<>(prefix);
        command.addAll(List.of(arguments));
        return List.copyOf(command);
    }
}
