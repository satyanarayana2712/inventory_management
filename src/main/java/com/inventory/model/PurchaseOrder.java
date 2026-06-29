package com.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Supplier purchase order. */
public record PurchaseOrder(int orderId, int supplierId, BigDecimal total, OrderStatus status,
                            LocalDate date, List<PurchaseOrderItem> items) {
}
