package com.games.theory.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProcessCommandRunner implements CommandRunner {
    @Override
    public CommandResult run(List<String> command, Duration timeout)
        throws IOException, InterruptedException, TimeoutException {
        Process process = new ProcessBuilder(command).start();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> stdout = executor.submit(() -> read(process.getInputStream()));
            Future<String> stderr = executor.submit(() -> read(process.getErrorStream()));
            try {
                if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                    process.destroyForcibly();
                    stdout.cancel(true);
                    stderr.cancel(true);
                    throw new TimeoutException("Command timed out: " + command.getFirst());
                }
                return new CommandResult(process.exitValue(), get(stdout), get(stderr));
            } catch (InterruptedException exception) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
                throw exception;
            }
        }
    }

    private static String read(java.io.InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static String get(Future<String> output) throws IOException, InterruptedException {
        try {
            return output.get();
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Unable to read command output", cause);
        }
    }
}
