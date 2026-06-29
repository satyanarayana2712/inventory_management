package com.inventory;

import com.inventory.cli.ConsoleApplication;
import com.inventory.service.ApplicationServices;
import java.util.logging.Logger;

/**
 * Application entry point for the Inventory Management System.
 */
public final class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Starts the application.
     *
     * @param args command-line arguments supplied by the runtime
     */
    public static void main(String[] args) {
        LOGGER.info("Inventory Management System starting...");
        ApplicationServices services = ApplicationServices.createDemo();
        new ConsoleApplication(services).run();
        services.shutdown();
    }
}
