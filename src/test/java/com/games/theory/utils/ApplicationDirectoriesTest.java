package com.games.theory.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationDirectoriesTest {
    @Test
    void resolvesWindowsLocalAppData() {
        ApplicationDirectories directories = ApplicationDirectories.resolve(
            "Windows 11",
            Map.of("LOCALAPPDATA", "C:\\Users\\tester\\AppData\\Local"),
            "C:\\Users\\tester"
        );

        assertEquals(
            Path.of("C:\\Users\\tester\\AppData\\Local").resolve("GamesTheoryAI"),
            directories.root()
        );
    }

    @Test
    void resolvesMacApplicationSupport() {
        ApplicationDirectories directories = ApplicationDirectories.resolve("Mac OS X", Map.of(), "/Users/tester");

        assertEquals(
            Path.of("/Users/tester/Library/Application Support/GamesTheoryAI"),
            directories.root()
        );
    }

    @Test
    void resolvesLinuxXdgDirectory() {
        ApplicationDirectories directories = ApplicationDirectories.resolve(
            "Linux",
            Map.of("XDG_DATA_HOME", "/tmp/xdg"),
            "/home/tester"
        );

        assertEquals(Path.of("/tmp/xdg/GamesTheoryAI"), directories.root());
    }

    @Test
    void fallsBackToLinuxUserDataDirectory() {
        ApplicationDirectories directories = ApplicationDirectories.resolve("Linux", Map.of(), "/home/tester");

        assertEquals(Path.of("/home/tester/.local/share/GamesTheoryAI"), directories.root());
        assertEquals(directories.root().resolve("runtime"), directories.runtime());
        assertEquals(directories.root().resolve("data"), directories.data());
    }
}
