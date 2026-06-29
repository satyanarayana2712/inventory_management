package com.inventory.model;

import java.math.BigDecimal;

/** Item sold in a sale. */
public record SaleItem(int itemId, int saleId, int productId, int quantity, BigDecimal sellingPrice) {
}
