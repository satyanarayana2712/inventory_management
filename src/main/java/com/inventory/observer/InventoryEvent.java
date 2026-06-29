package com.inventory.observer;

import java.time.LocalDateTime;

/** Domain event emitted by inventory services. */
public record InventoryEvent(String type, String message, LocalDateTime timestamp) {
    /** Creates an event stamped with the current time. */
    public static InventoryEvent now(String type, String message) {
        return new InventoryEvent(type, message, LocalDateTime.now());
    }
}
