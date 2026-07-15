package com.games.theory.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessCommandRunnerTest {
    @Test
    void capturesStandardErrorFromSuccessfulCommand() throws Exception {
        String executable = System.getProperty("os.name").toLowerCase().contains("win") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);

        CommandResult result = new ProcessCommandRunner().run(
            List.of(java.toString(), "-version"),
            Duration.ofSeconds(10)
        );

        assertTrue(result.successful());
        assertTrue(result.stderr().contains("version"));
    }
}
