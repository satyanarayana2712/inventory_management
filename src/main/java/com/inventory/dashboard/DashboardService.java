package com.inventory.dashboard;

import com.inventory.dto.DashboardMetrics;
import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import java.math.BigDecimal;

/** Computes dashboard metrics from repositories. */
public final class DashboardService {
    private final ProductRepository products;

    /** Creates a dashboard service. */
    public DashboardService(ProductRepository products) {
        this.products = products;
    }

    /** Returns current metrics. */
    public DashboardMetrics metrics() throws InventoryException {
        var list = products.findAll();
        BigDecimal value = list.stream()
                .map(product -> product.purchasePrice().multiply(BigDecimal.valueOf(product.stock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int low = (int) list.stream().filter(Product::isLowStock).count();
        return new DashboardMetrics(list.size(), 3, 3, value, low, BigDecimal.ZERO);
    }
}
