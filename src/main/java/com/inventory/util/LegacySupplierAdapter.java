package com.inventory.util;

import com.inventory.model.Supplier;

/** Adapter for importing legacy pipe-delimited supplier data. */
public final class LegacySupplierAdapter {
    /** Converts legacy text into a Supplier record. */
    public Supplier adapt(String legacy) {
        String[] parts = legacy.split("\\|");
        return new Supplier(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4]);
    }
}
