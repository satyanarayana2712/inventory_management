package com.inventory.builder;

import com.inventory.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Builder for products used by seed data and tests. */
public final class ProductBuilder {
    private int id;
    private String name = "Unnamed";
    private String description = "";
    private int categoryId = 1;
    private int supplierId = 1;
    private BigDecimal purchasePrice = BigDecimal.ZERO;
    private BigDecimal sellingPrice = BigDecimal.ZERO;
    private int stock;
    private int minimumStock;
    private String barcode = "ABC12345";
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProductBuilder id(int value) { id = value; return this; }
    public ProductBuilder name(String value) { name = value; return this; }
    public ProductBuilder prices(BigDecimal purchase, BigDecimal selling) { purchasePrice = purchase; sellingPrice = selling; return this; }
    public ProductBuilder stock(int value, int minimum) { stock = value; minimumStock = minimum; return this; }
    public ProductBuilder barcode(String value) { barcode = value; return this; }

    /** Builds the product. */
    public Product build() {
        return new Product(id, name, description, categoryId, supplierId, purchasePrice, sellingPrice,
                stock, minimumStock, barcode, createdAt);
    }
}
