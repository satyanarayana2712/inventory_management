package com.inventory.strategy;

import com.inventory.exception.InventoryException;

/** Strategy for report rendering. */
@FunctionalInterface
public interface ReportStrategy {
    /** Generates report text. */
    String generate() throws InventoryException;
}
