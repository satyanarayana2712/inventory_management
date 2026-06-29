package com.inventory.observer;

/** Observer for inventory events. */
@FunctionalInterface
public interface InventoryObserver {
    /** Handles an inventory event. */
    void onEvent(InventoryEvent event);
}
