package com.inventory.exception;

/** Raised when a role cannot perform an action. */
public class AuthorizationException extends InventoryException {
    private static final long serialVersionUID = 1L;

    public AuthorizationException(String message) {
        super(message);
    }
}
