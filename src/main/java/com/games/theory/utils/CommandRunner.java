package com.games.theory.utils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface CommandRunner {
    CommandResult run(List<String> command, Duration timeout)
        throws IOException, InterruptedException, TimeoutException;
}
