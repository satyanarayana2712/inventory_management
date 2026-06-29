package com.inventory.multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Runs report generation in the background with Callable/Future. */
public final class ReportExecutor implements AutoCloseable {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /** Submits a report job. */
    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
