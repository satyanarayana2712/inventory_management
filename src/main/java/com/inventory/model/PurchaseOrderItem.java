package com.inventory.model;

import java.math.BigDecimal;

/** Purchase order line item. */
public record PurchaseOrderItem(int orderItemId, int productId, int quantity, BigDecimal price) {
}
