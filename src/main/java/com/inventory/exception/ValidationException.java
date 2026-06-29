package com.inventory.exception;

/** Raised when input is invalid. */
public class ValidationException extends InventoryException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
