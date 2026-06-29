package com.inventory.dto;

import java.math.BigDecimal;

/** Dashboard aggregate values. */
public record DashboardMetrics(int totalProducts, int totalCategories, int totalSuppliers,
                               BigDecimal inventoryValue, int lowStockCount, BigDecimal todaysSales) {
}
