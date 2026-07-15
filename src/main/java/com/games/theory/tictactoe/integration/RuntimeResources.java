package com.games.theory.tictactoe.integration;

import java.io.IOException;
import java.nio.file.Path;

interface RuntimeResources {
    String read(String resource) throws IOException;

    void copy(String resource, Path destination) throws IOException;
}
