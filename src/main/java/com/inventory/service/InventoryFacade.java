package com.inventory.service;

import com.inventory.dashboard.DashboardService;
import com.inventory.multithreading.ReportExecutor;
import com.inventory.search.ProductSearchEngine;

/** Facade exposing high-level application capabilities. */
public record InventoryFacade(ProductService products, SalesService sales, PurchaseService purchases,
                              DashboardService dashboard, ProductSearchEngine search,
                              ReportExecutor reports) {
}
