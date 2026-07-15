package com.games.theory.tictactoe.integration;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
final class RuntimeLock implements AutoCloseable {
    private final FileChannel channel;
    private final FileLock lock;

    private RuntimeLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    static RuntimeLock acquire(Path lockPath, Duration timeout)
        throws IOException, InterruptedException, TimeoutException {
        FileChannel channel = FileChannel.open(
            lockPath,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE
        );
        try {
            return new RuntimeLock(channel, waitForLock(channel, timeout));
        } catch (IOException | InterruptedException | TimeoutException exception) {
            channel.close();
            throw exception;
        }
    }

    private static FileLock waitForLock(FileChannel channel, Duration timeout)
        throws IOException, InterruptedException, TimeoutException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            try {
                FileLock lock = channel.tryLock();
                if (lock != null) {
                    return lock;
                }
            } catch (OverlappingFileLockException exception) {
                log.debug("Python runtime lock is held by this process", exception);
            }
            Thread.sleep(100);
        }
        throw new TimeoutException("Timed out waiting for the Python runtime lock");
    }

    @Override
    public void close() throws IOException {
        try {
            lock.close();
        } finally {
            channel.close();
        }
    }
}
