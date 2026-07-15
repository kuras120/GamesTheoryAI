package com.games.theory.tictactoe.integration;

import com.games.theory.utils.CommandResult;
import com.games.theory.utils.CommandRunner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
final class PythonInterpreterDiscovery {
    static final int MINIMUM_PYTHON_MINOR = 9;

    private static final Pattern VERSION_PATTERN = Pattern.compile("Python\\s+(\\d+)\\.(\\d+)(?:\\.\\d+)?");
    private static final Duration DISCOVERY_TIMEOUT = Duration.ofSeconds(10);
    private static final String MISSING_PYTHON_MESSAGE =
        "AI unavailable. Install Python 3.9 or newer and restart the application.";
    private static final String SETUP_FAILED_MESSAGE =
        "AI unavailable. Setup failed. Restart the application.";

    private final CommandRunner commandRunner;
    private final String osName;

    PythonInterpreterDiscovery(CommandRunner commandRunner, String osName) {
        this.commandRunner = commandRunner;
        this.osName = osName;
    }

    PythonDiscoveryResult discover() {
        try {
            return discoverInterruptibly();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return PythonDiscoveryResult.unavailable(SETUP_FAILED_MESSAGE);
        }
    }

    private PythonDiscoveryResult discoverInterruptibly() throws InterruptedException {
        PythonVersion newestUnsupported = null;
        for (List<String> prefix : candidates(osName)) {
            Optional<PythonVersion> discoveredVersion = discoverVersion(prefix);
            if (discoveredVersion.isEmpty()) {
                continue;
            }
            PythonVersion version = discoveredVersion.get();
            if (version.supported()) {
                return PythonDiscoveryResult.available(prefix);
            }
            newestUnsupported = newerVersion(newestUnsupported, version);
        }
        return unavailableResult(newestUnsupported);
    }

    private Optional<PythonVersion> discoverVersion(List<String> prefix) throws InterruptedException {
        try {
            CommandResult result = commandRunner.run(append(prefix, "--version"), DISCOVERY_TIMEOUT);
            return parseSuccessfulVersion(result);
        } catch (IOException | TimeoutException exception) {
            log.debug("Python candidate is unavailable: {}", prefix, exception);
            return Optional.empty();
        }
    }

    private static PythonVersion newerVersion(PythonVersion current, PythonVersion candidate) {
        return current == null || candidate.compareTo(current) > 0 ? candidate : current;
    }

    private static PythonDiscoveryResult unavailableResult(PythonVersion newestUnsupported) {
        if (newestUnsupported == null) {
            return PythonDiscoveryResult.unavailable(MISSING_PYTHON_MESSAGE);
        }
        return PythonDiscoveryResult.unavailable(
            "AI unavailable. Detected Python " + newestUnsupported
                + "; Python 3.9 or newer is required."
        );
    }

    private static Optional<PythonVersion> parseSuccessfulVersion(CommandResult result) {
        return result.successful()
            ? parseVersion(result.stdout() + "\n" + result.stderr())
            : Optional.empty();
    }

    static List<List<String>> candidates(String osName) {
        List<List<String>> candidates = new ArrayList<>();
        if (osName.toLowerCase(Locale.ROOT).contains("win")) {
            candidates.add(List.of("py", "-3"));
        }
        candidates.add(List.of("python3"));
        candidates.add(List.of("python"));
        return List.copyOf(candidates);
    }

    static Optional<PythonVersion> parseVersion(String output) {
        Matcher matcher = VERSION_PATTERN.matcher(output);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(new PythonVersion(
            Integer.parseInt(matcher.group(1)),
            Integer.parseInt(matcher.group(2))
        ));
    }

    private static List<String> append(List<String> prefix, String argument) {
        List<String> command = new ArrayList<>(prefix);
        command.add(argument);
        return List.copyOf(command);
    }

    record PythonVersion(int major, int minor) implements Comparable<PythonVersion> {
        boolean supported() {
            return major == 3 && minor >= MINIMUM_PYTHON_MINOR;
        }

        @Override
        public int compareTo(PythonVersion other) {
            int majorComparison = Integer.compare(major, other.major);
            return majorComparison != 0 ? majorComparison : Integer.compare(minor, other.minor);
        }

        @Override
        public String toString() {
            return major + "." + minor;
        }
    }
}
