package com.games.theory.tictactoe.integration;

import java.util.List;

record PythonDiscoveryResult(List<String> commandPrefix, String failureMessage) {
    static PythonDiscoveryResult available(List<String> commandPrefix) {
        return new PythonDiscoveryResult(List.copyOf(commandPrefix), null);
    }

    static PythonDiscoveryResult unavailable(String failureMessage) {
        return new PythonDiscoveryResult(List.of(), failureMessage);
    }

    boolean available() {
        return !commandPrefix.isEmpty();
    }
}
