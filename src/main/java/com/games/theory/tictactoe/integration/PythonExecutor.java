package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.utils.CommandResult;
import com.games.theory.utils.CommandRunner;
import com.games.theory.utils.ProcessCommandRunner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PythonExecutor {
    private static final Duration MOVE_TIMEOUT = Duration.ofMinutes(1);
    private static final Pattern MOVE_PATTERN = Pattern.compile(
        "\\{\\s*\\\"x\\\"\\s*:\\s*(-?\\d+)\\s*,\\s*\\\"y\\\"\\s*:\\s*(-?\\d+)\\s*}"
    );

    private final CommandRunner commandRunner;

    public PythonExecutor() {
        this(new ProcessCommandRunner());
    }

    PythonExecutor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    public AiMove processState(PythonRuntime runtime, String pointsX, String pointsO, String... aiMap) {
        List<String> command = new ArrayList<>();
        command.add(runtime.gamesTheoryExecutable().toString());
        command.add("--config");
        command.add(runtime.applicationDataDirectory().toString());
        command.add(pointsX);
        command.add(pointsO);
        Arrays.stream(aiMap).map(mark -> mark == null ? "N" : mark).forEach(command::add);
        log.debug("Executing AI command: {}", command);

        try {
            CommandResult result = commandRunner.run(List.copyOf(command), MOVE_TIMEOUT);
            if (!result.stderr().isBlank()) {
                log.debug("AI diagnostic output:\n{}", result.stderr());
            }
            if (!result.successful()) {
                throw new AiException("AI process exited with code " + result.exitCode());
            }
            return parseMove(result.stdout());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AiException("AI process was interrupted", exception);
        } catch (IOException | TimeoutException exception) {
            throw new AiException("Unable to execute AI process", exception);
        }
    }

    static AiMove parseMove(String stdout) {
        Matcher matcher = MOVE_PATTERN.matcher(stdout.trim());
        if (!matcher.matches()) {
            throw new AiException("Invalid AI output: expected JSON with numeric x and y coordinates");
        }
        return new AiMove(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }
}
