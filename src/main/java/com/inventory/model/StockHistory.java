package com.inventory.model;

import java.time.LocalDateTime;

/** Auditable stock movement. */
public record StockHistory(int historyId, int productId, int previousStock, int newStock,
                           StockOperation operation, LocalDateTime timestamp) {
}
