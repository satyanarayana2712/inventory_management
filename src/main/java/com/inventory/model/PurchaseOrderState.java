package com.inventory.model;

/** State pattern for purchase order transitions. */
public interface PurchaseOrderState {
    /** Returns the next status after receive. */
    OrderStatus receive();
}
