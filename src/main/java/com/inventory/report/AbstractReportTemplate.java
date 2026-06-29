package com.inventory.report;

import com.inventory.exception.InventoryException;

/** Template method for report generation. */
public abstract class AbstractReportTemplate {
    /** Generates a complete report. */
    public final String render() throws InventoryException {
        return header() + body() + footer();
    }

    protected String header() {
        return "==== Report ====\n";
    }

    protected abstract String body() throws InventoryException;

    protected String footer() {
        return "==== End ====\n";
    }
}
