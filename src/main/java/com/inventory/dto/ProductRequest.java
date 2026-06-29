package com.inventory.dto;

import java.math.BigDecimal;

/** Product create/update command. */
public record ProductRequest(String name, String description, int categoryId, int supplierId,
                             BigDecimal purchasePrice, BigDecimal sellingPrice, int stock,
                             int minimumStock, String barcode) {
}
