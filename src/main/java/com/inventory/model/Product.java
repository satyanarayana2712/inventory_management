package com.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Product stored in inventory. */
public final class Product implements Comparable<Product> {
    private final int productId;
    private final String name;
    private final String description;
    private final int categoryId;
    private final int supplierId;
    private final BigDecimal purchasePrice;
    private final BigDecimal sellingPrice;
    private final int stock;
    private final int minimumStock;
    private final String barcode;
    private final LocalDateTime createdAt;

    /** Creates a product. */
    public Product(int productId, String name, String description, int categoryId, int supplierId,
                   BigDecimal purchasePrice, BigDecimal sellingPrice, int stock, int minimumStock,
                   String barcode, LocalDateTime createdAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
        this.minimumStock = minimumStock;
        this.barcode = barcode;
        this.createdAt = createdAt;
    }

    public int productId() { return productId; }
    public String name() { return name; }
    public String description() { return description; }
    public int categoryId() { return categoryId; }
    public int supplierId() { return supplierId; }
    public BigDecimal purchasePrice() { return purchasePrice; }
    public BigDecimal sellingPrice() { return sellingPrice; }
    public int stock() { return stock; }
    public int minimumStock() { return minimumStock; }
    public String barcode() { return barcode; }
    public LocalDateTime createdAt() { return createdAt; }

    /** Returns whether the item has reached its reorder threshold. */
    public boolean isLowStock() {
        return stock <= minimumStock;
    }

    /** Returns a copy with a new stock value. */
    public Product withStock(int newStock) {
        return new Product(productId, name, description, categoryId, supplierId, purchasePrice,
                sellingPrice, newStock, minimumStock, barcode, createdAt);
    }

    @Override
    public int compareTo(Product other) {
        return name.compareToIgnoreCase(other.name);
    }
}
