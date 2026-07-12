package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.utils.DataReaderUtils;
import com.games.theory.utils.FileType;
import com.games.theory.utils.LoggerUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class PythonExecutor {
    private static final Pattern MOVE_PATTERN = Pattern.compile(
        "\\{\\s*\\\"x\\\"\\s*:\\s*(-?\\d+)\\s*,\\s*\\\"y\\\"\\s*:\\s*(-?\\d+)\\s*}"
    );
    private Process process;

    public void initialize(String... command) {
        try {
            // TODO skip for tests
            process = new ProcessBuilder(command).start();
            if (process.waitFor(1, TimeUnit.MINUTES)) {
                LoggerUtils.processLog(process);
                log.info("AI env installation completed");
            } else {
                process.destroy();
                throw new TimeoutException("Time exceeded for AI env installation process");
            }
        } catch (Exception ex) {
            log.error("AI error {}", ex.getMessage());
            throw new AiException("AI error", ex);
        }
    }

    public AiMove processState(String pointsX, String pointsO, String... aiMap) {
        try {
            List<String> command = Stream.concat(
                Stream.of(
                    DataReaderUtils.getScript(FileType.GAMES_THEORY).getPath(),
                    pointsX,
                    pointsO
                ),
                Arrays.stream(aiMap).map(mark -> mark == null ? "N" : mark)
            ).toList();
            log.debug(command.toString());
            process = new ProcessBuilder(command).start();
            CompletableFuture<String> stdoutFuture = readStream(process.getInputStream());
            CompletableFuture<String> stderrFuture = readStream(process.getErrorStream());
            int exitCode = process.waitFor();
            String stdout = stdoutFuture.join();
            String stderr = stderrFuture.join();
            if (!stderr.isBlank()) {
                log.debug("AI diagnostic output:\n{}", stderr);
            }
            if (exitCode != 0) {
                throw new AiException("AI process exited with code " + exitCode);
            }
            return parseMove(stdout);
        } catch (Exception ex) {
            log.error("AI error {}", ex.getMessage());
            if (ex instanceof AiException aiException) {
                throw aiException;
            }
            throw new AiException("AI error", ex);
        }
    }

    static AiMove parseMove(String stdout) {
        Matcher matcher = MOVE_PATTERN.matcher(stdout.trim());
        if (!matcher.matches()) {
            throw new AiException("Invalid AI output: expected JSON with numeric x and y coordinates");
        }
        return new AiMove(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    private static CompletableFuture<String> readStream(java.io.InputStream stream) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (java.io.IOException exception) {
                throw new AiException("Unable to read AI process output", exception);
            }
        });
    }
}
