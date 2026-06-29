package com.inventory.report;

import com.inventory.exception.InventoryException;
import com.inventory.strategy.ReportStrategy;
import java.time.LocalDateTime;

/** Decorates reports with metadata. */
public final class ReportDecorator implements ReportStrategy {
    private final ReportStrategy delegate;

    /** Creates a decorator. */
    public ReportDecorator(ReportStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public String generate() throws InventoryException {
        return "Generated at " + LocalDateTime.now() + "\n" + delegate.generate();
    }
}
