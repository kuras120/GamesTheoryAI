package com.games.theory.utils;

public record CommandResult(int exitCode, String stdout, String stderr) {
    public boolean successful() {
        return exitCode == 0;
    }
}
