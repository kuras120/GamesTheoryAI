package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.utils.ApplicationDirectories;
import com.games.theory.utils.CommandResult;
import com.games.theory.utils.CommandRunner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
final class WheelhouseInstaller {
    static final String WHEELHOUSE_RESOURCE = "python-wheelhouse";
    static final String MANIFEST_FILENAME = "wheelhouse-manifest.txt";

    private static final Duration INSTALL_TIMEOUT = Duration.ofMinutes(2);

    private final ApplicationDirectories directories;
    private final CommandRunner commandRunner;
    private final RuntimeResources resources;

    WheelhouseInstaller(
        ApplicationDirectories directories,
        CommandRunner commandRunner,
        RuntimeResources resources
    ) {
        this.directories = directories;
        this.commandRunner = commandRunner;
        this.resources = resources;
    }

    void install(PythonRuntime runtime, boolean reusable)
        throws IOException, InterruptedException, TimeoutException, NoSuchAlgorithmException {
        String manifestContent = resources.read(resourcePath(MANIFEST_FILENAME));
        WheelhouseManifest manifest = WheelhouseManifest.parse(manifestContent);
        Path wheelhouse = directories.runtime().resolve("wheelhouse");
        extractAndVerify(manifest, manifestContent, wheelhouse);

        String manifestHash = RuntimeFileOperations.sha256(manifestContent);
        Path marker = directories.runtime().resolve("installed-wheelhouse.sha256");
        if (!reusable || !RuntimeFileOperations.markerMatches(marker, manifestHash)) {
            installRequirement(runtime, wheelhouse, manifest.requirement());
            RuntimeFileOperations.writeMarker(marker, manifestHash);
        }
    }

    private void extractAndVerify(
        WheelhouseManifest manifest,
        String manifestContent,
        Path wheelhouse
    ) throws IOException, NoSuchAlgorithmException {
        RuntimeFileOperations.deleteDirectory(wheelhouse);
        Files.createDirectories(wheelhouse);
        Files.writeString(wheelhouse.resolve(MANIFEST_FILENAME), manifestContent);
        for (WheelhouseManifest.WheelArtifact wheel : manifest.wheels()) {
            Path destination = wheelhouse.resolve(wheel.filename());
            resources.copy(resourcePath(wheel.filename()), destination);
            if (!wheel.sha256().equals(RuntimeFileOperations.sha256(destination))) {
                throw new AiException("Python wheel hash mismatch: " + wheel.filename());
            }
        }
    }

    private void installRequirement(PythonRuntime runtime, Path wheelhouse, String requirement)
        throws IOException, InterruptedException, TimeoutException {
        List<String> command = List.of(
            runtime.pythonExecutable().toString(),
            "-m",
            "pip",
            "install",
            "--disable-pip-version-check",
            "--no-index",
            "--find-links",
            wheelhouse.toString(),
            "--upgrade",
            requirement
        );
        CommandResult result = commandRunner.run(command, INSTALL_TIMEOUT);
        if (!result.stderr().isBlank()) {
            log.debug("Python package installation diagnostics:\n{}", result.stderr());
        }
        if (!result.successful()) {
            throw new IOException("Unable to install the Python runtime package: " + result.stderr());
        }
    }

    private static String resourcePath(String filename) {
        return WHEELHOUSE_RESOURCE + "/" + filename;
    }
}
