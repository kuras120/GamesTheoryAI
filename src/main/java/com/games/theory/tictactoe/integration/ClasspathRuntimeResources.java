package com.games.theory.tictactoe.integration;

import com.games.theory.utils.DataReaderUtils;

import java.io.IOException;
import java.nio.file.Path;

final class ClasspathRuntimeResources implements RuntimeResources {
    @Override
    public String read(String resource) throws IOException {
        return DataReaderUtils.readResource(resource);
    }

    @Override
    public void copy(String resource, Path destination) throws IOException {
        DataReaderUtils.copyResource(resource, destination);
    }
}
