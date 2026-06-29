package com.inventory.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Observer-pattern event publisher. */
public final class EventPublisher {
    private final List<InventoryObserver> observers = new CopyOnWriteArrayList<>();

    /** Adds an observer. */
    public void subscribe(InventoryObserver observer) {
        observers.add(observer);
    }

    /** Publishes an event to all observers. */
    public void publish(InventoryEvent event) {
        observers.forEach(observer -> observer.onEvent(event));
    }
}
