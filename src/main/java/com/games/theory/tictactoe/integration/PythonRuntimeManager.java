package com.games.theory.tictactoe.integration;

import com.games.theory.utils.ApplicationDirectories;
import com.games.theory.utils.CommandRunner;
import com.games.theory.utils.ProcessCommandRunner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
public class PythonRuntimeManager {
    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
    private static final String MISSING_VENV_MESSAGE =
        "AI unavailable. Python virtual environment support is missing. Install it and restart the application.";
    private static final String SETUP_FAILED_MESSAGE =
        "AI unavailable. Setup failed. Restart the application.";

    private final ApplicationDirectories directories;
    private final PythonInterpreterDiscovery interpreterDiscovery;
    private final PythonEnvironmentManager environmentManager;
    private final WheelhouseInstaller wheelhouseInstaller;

    public PythonRuntimeManager() {
        this(
            ApplicationDirectories.resolve(),
            new ProcessCommandRunner(),
            new ClasspathRuntimeResources(),
            System.getProperty("os.name")
        );
    }

    PythonRuntimeManager(
        ApplicationDirectories directories,
        CommandRunner commandRunner,
        RuntimeResources resources,
        String osName
    ) {
        this.directories = directories;
        interpreterDiscovery = new PythonInterpreterDiscovery(commandRunner, osName);
        environmentManager = new PythonEnvironmentManager(directories, commandRunner, osName);
        wheelhouseInstaller = new WheelhouseInstaller(directories, commandRunner, resources);
    }

    public PythonRuntimeResult prepare() {
        PythonDiscoveryResult discovery = interpreterDiscovery.discover();
        if (!discovery.available()) {
            return PythonRuntimeResult.unavailable(discovery.failureMessage());
        }
        try {
            return PythonRuntimeResult.available(prepare(discovery.commandPrefix()));
        } catch (VenvUnavailableException exception) {
            log.error("Python virtual environment support is unavailable", exception);
            return PythonRuntimeResult.unavailable(MISSING_VENV_MESSAGE);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.error("Python runtime preparation was interrupted", exception);
            return PythonRuntimeResult.unavailable(SETUP_FAILED_MESSAGE);
        } catch (Exception exception) {
            log.error("Unable to prepare Python runtime", exception);
            return PythonRuntimeResult.unavailable(SETUP_FAILED_MESSAGE);
        }
    }

    private PythonRuntime prepare(List<String> pythonCommand)
        throws IOException, InterruptedException, TimeoutException, NoSuchAlgorithmException,
        VenvUnavailableException {
        Files.createDirectories(directories.runtime());
        try (RuntimeLock ignored = RuntimeLock.acquire(
            directories.runtime().resolve("bootstrap.lock"),
            LOCK_TIMEOUT
        )) {
            PreparedPythonRuntime prepared = environmentManager.prepare(pythonCommand);
            wheelhouseInstaller.install(prepared.runtime(), prepared.reusable());
            environmentManager.initialize(prepared.runtime());
            return prepared.runtime();
        }
    }
}
