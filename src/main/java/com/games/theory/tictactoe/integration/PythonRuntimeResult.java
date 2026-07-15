package com.games.theory.tictactoe.integration;

public record PythonRuntimeResult(PythonRuntime runtime, String statusMessage) {
    public static PythonRuntimeResult available(PythonRuntime runtime) {
        return new PythonRuntimeResult(runtime, "AI available");
    }

    public static PythonRuntimeResult unavailable(String statusMessage) {
        return new PythonRuntimeResult(null, statusMessage);
    }

    public boolean isAvailable() {
        return runtime != null;
    }
}
