package com.games.theory.tictactoe.integration;

import java.nio.file.Path;

public record PythonRuntime(
    Path pythonExecutable,
    Path gamesTheoryExecutable,
    Path gamesTheoryInitExecutable,
    Path applicationDataDirectory
) {
}
