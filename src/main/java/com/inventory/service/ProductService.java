package com.inventory.service;

import com.inventory.dto.ProductRequest;
import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.multithreading.AuditLogService;
import com.inventory.observer.EventPublisher;
import com.inventory.observer.InventoryEvent;
import com.inventory.repository.ProductRepository;
import com.inventory.validation.InputValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/** Product business operations. */
public final class ProductService {
    private final ProductRepository products;
    private final EventPublisher events;
    private final AuditLogService audit;
    private final ReentrantLock stockLock = new ReentrantLock();

    /** Creates a product service. */
    public ProductService(ProductRepository products, EventPublisher events, AuditLogService audit) {
        this.products = products;
        this.events = events;
        this.audit = audit;
    }

    /** Creates a product after validation and duplicate checks. */
    public Product create(ProductRequest request, String actor) throws InventoryException {
        InputValidator.product(request);
        if (products.findByName(request.name()).stream().anyMatch(p -> p.name().equalsIgnoreCase(request.name()))) {
            throw new InventoryException("Duplicate product name");
        }
        Product product = new Product(0, request.name(), request.description(), request.categoryId(), request.supplierId(),
                request.purchasePrice(), request.sellingPrice(), request.stock(), request.minimumStock(),
                request.barcode(), LocalDateTime.now());
        Product saved = products.save(product);
        audit.log(actor, "Created product " + saved.name());
        events.publish(InventoryEvent.now("PRODUCT_CREATED", saved.name()));
        return saved;
    }

    /** Lists products. */
    public List<Product> list() throws InventoryException {
        return products.findAll();
    }

    /** Finds a product by barcode. */
    public Optional<Product> byBarcode(String barcode) throws InventoryException {
        return products.findByBarcode(barcode);
    }

    /** Applies stock delta with synchronization. */
    public Product adjustStock(int productId, int delta, String actor) throws InventoryException {
        stockLock.lock();
        try {
            Product current = products.findById(productId).orElseThrow(() -> new InventoryException("Product not found"));
            int newStock = current.stock() + delta;
            if (newStock < 0) {
                throw new InventoryException("Stock cannot become negative");
            }
            products.updateStock(productId, newStock);
            Product updated = current.withStock(newStock);
            audit.log(actor, "Adjusted stock for " + current.name() + " to " + newStock);
            if (updated.isLowStock()) {
                events.publish(InventoryEvent.now("LOW_STOCK", updated.name() + " reached minimum stock"));
            }
            return updated;
        } finally {
            stockLock.unlock();
        }
    }
}
