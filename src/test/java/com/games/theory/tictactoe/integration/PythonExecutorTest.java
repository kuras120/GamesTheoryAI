package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.utils.CommandResult;
import com.games.theory.utils.CommandRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PythonExecutorTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void parsesMoveFromStandardOutput() {
        assertEquals(new AiMove(2, 3), PythonExecutor.parseMove("  {\"x\": 2, \"y\": 3}\n"));
    }

    @Test
    void rejectsLogsMixedIntoStandardOutput() {
        assertThrows(AiException.class, () ->
            PythonExecutor.parseMove("debug message\n{\"x\": 2, \"y\": 3}"));
    }

    @Test
    void rejectsNonNumericCoordinates() {
        assertThrows(AiException.class, () ->
            PythonExecutor.parseMove("{\"x\": \"2\", \"y\": 3}"));
    }

    @Test
    void invokesRuntimeWithExplicitConfigAndNormalizedBoard() {
        RecordingRunner runner = new RecordingRunner(new CommandResult(0, "{\"x\": 1, \"y\": 2}", "diagnostic"));
        PythonRuntime runtime = runtime();

        AiMove move = new PythonExecutor(runner).processState(runtime, "4", "2", "X", "", "O");

        assertEquals(new AiMove(1, 2), move);
        assertEquals(List.of(
            runtime.gamesTheoryExecutable().toString(),
            "--config",
            runtime.applicationDataDirectory().toString(),
            "4",
            "2",
            "X",
            "N",
            "O"
        ), runner.command);
    }

    @Test
    void rejectsNonZeroProcessExit() {
        RecordingRunner runner = new RecordingRunner(new CommandResult(2, "", "failure"));

        assertThrows(AiException.class, () ->
            new PythonExecutor(runner).processState(runtime(), "0", "0", "X"));
    }

    private PythonRuntime runtime() {
        return new PythonRuntime(
            temporaryDirectory.resolve("python"),
            temporaryDirectory.resolve("games-theory"),
            temporaryDirectory.resolve("games-theory-init"),
            temporaryDirectory.resolve("application-data")
        );
    }

    private static class RecordingRunner implements CommandRunner {
        private final CommandResult result;
        private List<String> command;

        private RecordingRunner(CommandResult result) {
            this.result = result;
        }

        @Override
        public CommandResult run(List<String> command, Duration timeout) {
            this.command = command;
            return result;
        }
    }
}
