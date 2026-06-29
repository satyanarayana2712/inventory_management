package com.inventory.multithreading;

import com.inventory.observer.InventoryEvent;
import com.inventory.observer.InventoryObserver;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/** Background notification worker backed by a blocking queue. */
public final class NotificationService implements InventoryObserver, AutoCloseable {
    private final BlockingQueue<InventoryEvent> queue = new LinkedBlockingQueue<>();
    private final Deque<InventoryEvent> recent = new ArrayDeque<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    /** Starts the notification consumer. */
    public NotificationService() {
        executor.submit(this::consume);
    }

    @Override
    public void onEvent(InventoryEvent event) {
        queue.offer(event);
    }

    /** Returns recent notifications. */
    public synchronized Deque<InventoryEvent> recentNotifications() {
        return new ArrayDeque<>(recent);
    }

    private void consume() {
        while (running || !queue.isEmpty()) {
            try {
                InventoryEvent event = queue.poll(1, TimeUnit.SECONDS);
                if (event != null) {
                    synchronized (this) {
                        recent.push(event);
                        while (recent.size() > 25) {
                            recent.removeLast();
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    @Override
    public void close() {
        running = false;
        executor.shutdown();
    }
}
