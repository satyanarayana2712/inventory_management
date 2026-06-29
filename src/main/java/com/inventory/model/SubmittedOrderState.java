package com.inventory.model;

/** Submitted purchase-order state. */
public final class SubmittedOrderState implements PurchaseOrderState {
    @Override
    public OrderStatus receive() {
        return OrderStatus.RECEIVED;
    }
}
