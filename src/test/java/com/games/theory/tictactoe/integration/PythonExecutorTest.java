package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PythonExecutorTest {

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
}
