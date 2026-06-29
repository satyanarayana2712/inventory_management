package com.inventory.multithreading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Non-blocking producer-consumer audit logger. */
public final class AuditLogService implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(AuditLogService.class.getName());
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;
    private final Path logFile;

    /** Creates and starts the audit worker. */
    public AuditLogService(Path logFile) {
        this.logFile = logFile;
        executor.submit(this::consume);
    }

    /** Queues an audit entry without blocking business operations. */
    public void log(String user, String action) {
        queue.offer(LocalDateTime.now() + " | " + user + " | " + action);
    }

    private void consume() {
        while (running || !queue.isEmpty()) {
            try {
                String entry = queue.poll(1, TimeUnit.SECONDS);
                if (entry != null) {
                    Files.createDirectories(logFile.getParent());
                    Files.writeString(logFile, entry + System.lineSeparator(),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Unable to write audit log", ex);
            }
        }
    }

    @Override
    public void close() {
        running = false;
        executor.shutdown();
    }
}
