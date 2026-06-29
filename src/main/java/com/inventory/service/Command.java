package com.inventory.service;

import com.inventory.exception.InventoryException;

/** Command pattern contract for business actions. */
@FunctionalInterface
public interface Command {
    /** Executes the command. */
    void execute() throws InventoryException;
}
