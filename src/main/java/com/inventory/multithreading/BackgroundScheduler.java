package com.inventory.multithreading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Runs auto-save and cleanup jobs with ScheduledExecutorService. */
public final class BackgroundScheduler implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(BackgroundScheduler.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /** Starts scheduled jobs. */
    public void start() {
        scheduler.scheduleAtFixedRate(this::autoSave, 30, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.DAYS);
    }

    private void autoSave() {
        try {
            Files.createDirectories(Path.of("autosave"));
            Files.writeString(Path.of("autosave", "state.txt"), "saved=" + LocalDateTime.now());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Auto-save failed", ex);
        }
    }

    private void cleanup() {
        LOGGER.info("Scheduled cleanup completed");
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }
}
