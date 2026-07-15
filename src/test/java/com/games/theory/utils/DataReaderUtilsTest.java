package com.games.theory.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataReaderUtilsTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void readsAndCopiesClasspathResource() throws Exception {
        Path destination = temporaryDirectory.resolve("nested/copied-resource.txt");

        String content = DataReaderUtils.readResource("sample-resource.txt");
        DataReaderUtils.copyResource("sample-resource.txt", destination);

        assertEquals("resource-content\n", content);
        assertEquals(content, Files.readString(destination));
    }
}
