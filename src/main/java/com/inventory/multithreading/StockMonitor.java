package com.inventory.multithreading;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.observer.EventPublisher;
import com.inventory.observer.InventoryEvent;
import com.inventory.repository.ProductRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/** Periodically scans stock levels and emits low-stock events. */
public final class StockMonitor implements AutoCloseable {
    private final ProductRepository repository;
    private final EventPublisher publisher;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Semaphore scanPermit = new Semaphore(1);
    private volatile boolean running;

    /** Creates a monitor. */
    public StockMonitor(ProductRepository repository, EventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    /** Starts monitoring. */
    public void start() {
        running = true;
        executor.submit(this::loop);
    }

    private void loop() {
        while (running) {
            try {
                if (scanPermit.tryAcquire(1, TimeUnit.SECONDS)) {
                    try {
                        for (Product product : repository.findAll()) {
                            if (product.isLowStock()) {
                                publisher.publish(InventoryEvent.now("LOW_STOCK", product.name() + " is low"));
                            }
                        }
                    } finally {
                        scanPermit.release();
                    }
                }
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (InventoryException ignored) {
                running = false;
            }
        }
    }

    @Override
    public void close() {
        running = false;
        executor.shutdownNow();
    }
}
