package com.inventory.report;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.strategy.ReportStrategy;

/** Inventory report implementation. */
public final class InventoryReportStrategy implements ReportStrategy {
    private final ProductRepository products;

    /** Creates an inventory report. */
    public InventoryReportStrategy(ProductRepository products) {
        this.products = products;
    }

    @Override
    public String generate() throws InventoryException {
        StringBuilder builder = new StringBuilder("Inventory Report\n");
        for (Product product : products.findAll()) {
            builder.append(product.productId()).append(',')
                    .append(product.name()).append(',')
                    .append(product.stock()).append(',')
                    .append(product.sellingPrice()).append('\n');
        }
        return builder.toString();
    }
}
