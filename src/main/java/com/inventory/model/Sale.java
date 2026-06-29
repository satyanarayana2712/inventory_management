package com.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Completed customer sale. */
public record Sale(int saleId, BigDecimal total, LocalDateTime createdAt, List<SaleItem> items) {
}
