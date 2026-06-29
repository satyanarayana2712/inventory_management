package com.inventory.service;

import com.inventory.exception.InventoryException;
import com.inventory.model.OrderStatus;
import com.inventory.model.PurchaseOrder;
import com.inventory.model.PurchaseOrderItem;
import com.inventory.multithreading.AuditLogService;
import com.inventory.observer.EventPublisher;
import com.inventory.observer.InventoryEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Handles purchase orders and receiving stock. */
public final class PurchaseService {
    private final ProductService productService;
    private final AuditLogService audit;
    private final EventPublisher events;
    private final AtomicInteger orderIds = new AtomicInteger();

    /** Creates a purchase service. */
    public PurchaseService(ProductService productService, AuditLogService audit, EventPublisher events) {
        this.productService = productService;
        this.audit = audit;
        this.events = events;
    }

    /** Receives a purchase order and updates inventory. */
    public PurchaseOrder receive(int supplierId, List<PurchaseOrderItem> items, String actor) throws InventoryException {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderItem item : items) {
            productService.adjustStock(item.productId(), item.quantity(), actor);
            total = total.add(item.price().multiply(BigDecimal.valueOf(item.quantity())));
        }
        PurchaseOrder order = new PurchaseOrder(orderIds.incrementAndGet(), supplierId, total,
                OrderStatus.RECEIVED, LocalDate.now(), List.copyOf(items));
        audit.log(actor, "Received purchase order #" + order.orderId());
        events.publish(InventoryEvent.now("PURCHASE_RECEIVED", "Purchase order #" + order.orderId() + " received"));
        return order;
    }
}
