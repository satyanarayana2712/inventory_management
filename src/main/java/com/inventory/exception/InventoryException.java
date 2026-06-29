package com.inventory.exception;

/** Base exception for inventory failures. */
public class InventoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
