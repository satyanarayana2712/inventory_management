package com.inventory.service;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.multithreading.AuditLogService;
import com.inventory.observer.EventPublisher;
import com.inventory.observer.InventoryEvent;
import com.inventory.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Handles sales and automatic stock deduction. */
public final class SalesService {
    private final ProductRepository products;
    private final AuditLogService audit;
    private final EventPublisher events;
    private final AtomicInteger saleIds = new AtomicInteger();

    /** Creates a sales service. */
    public SalesService(ProductRepository products, AuditLogService audit, EventPublisher events) {
        this.products = products;
        this.audit = audit;
        this.events = events;
    }

    /** Creates a sale from product-id to quantity. */
    public synchronized Sale createSale(Map<Integer, Integer> quantities, String actor) throws InventoryException {
        BigDecimal total = BigDecimal.ZERO;
        int saleId = saleIds.incrementAndGet();
        java.util.ArrayList<SaleItem> items = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            Product product = products.findById(entry.getKey()).orElseThrow(() -> new InventoryException("Product not found"));
            int quantity = entry.getValue();
            if (product.stock() < quantity) {
                throw new InventoryException("Insufficient stock for " + product.name());
            }
            products.updateStock(product.productId(), product.stock() - quantity);
            total = total.add(product.sellingPrice().multiply(BigDecimal.valueOf(quantity)));
            items.add(new SaleItem(0, saleId, product.productId(), quantity, product.sellingPrice()));
        }
        Sale sale = new Sale(saleId, total, LocalDateTime.now(), List.copyOf(items));
        audit.log(actor, "Completed sale #" + saleId + " total " + total);
        events.publish(InventoryEvent.now("SALE_COMPLETED", "Sale #" + saleId + " completed"));
        return sale;
    }
}
