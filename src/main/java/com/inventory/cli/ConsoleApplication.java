package com.inventory.cli;

import com.inventory.dto.ProductRequest;
import com.inventory.exception.InventoryException;
import com.inventory.model.User;
import com.inventory.report.CsvExporter;
import com.inventory.report.ReportDecorator;
import com.inventory.service.ApplicationServices;
import com.inventory.service.InventoryFacade;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Future;

/** Console UI shell for environments where Swing is not desired. */
public final class ConsoleApplication {
    private final ApplicationServices services;
    private final Scanner scanner = new Scanner(System.in);

    /** Creates the console application. */
    public ConsoleApplication(ApplicationServices services) {
        this.services = services;
    }

    /** Runs the application loop. */
    public void run() {
        System.out.println("Inventory Management System");
        Optional<User> user = login();
        if (user.isEmpty()) {
            System.out.println("Login failed.");
            return;
        }
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                running = handle(choice, user.get());
            } catch (InventoryException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private Optional<User> login() {
        System.out.print("Username [admin]: ");
        String username = defaultValue(scanner.nextLine(), "admin");
        System.out.print("Password [admin123]: ");
        String password = defaultValue(scanner.nextLine(), "admin123");
        try {
            return services.authService().login(username, password.toCharArray());
        } catch (InventoryException ex) {
            return Optional.empty();
        }
    }

    private boolean handle(String choice, User user) throws InventoryException {
        InventoryFacade facade = services.facade();
        switch (choice) {
            case "1" -> facade.products().list().forEach(product ->
                    System.out.printf("%d | %s | stock=%d | price=%s%n",
                            product.productId(), product.name(), product.stock(), product.sellingPrice()));
            case "2" -> addProduct(user);
            case "3" -> {
                System.out.print("Product id: ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.print("Delta: ");
                int delta = Integer.parseInt(scanner.nextLine());
                facade.products().adjustStock(id, delta, user.username());
            }
            case "4" -> System.out.println(facade.dashboard().metrics());
            case "5" -> facade.search().lowStock(facade.products().list()).forEach(product -> System.out.println(product.name()));
            case "6" -> createSale(user);
            case "7" -> exportReport();
            case "8" -> services.notifications().recentNotifications().forEach(System.out::println);
            case "0" -> { return false; }
            default -> System.out.println("Unknown option");
        }
        return true;
    }

    private void addProduct(User user) throws InventoryException {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Barcode: ");
        String barcode = scanner.nextLine();
        ProductRequest request = new ProductRequest(name, "", 1, 1,
                new BigDecimal("1.00"), new BigDecimal("2.00"), 10, 2, barcode);
        services.facade().products().create(request, user.username());
    }

    private void createSale(User user) throws InventoryException {
        System.out.print("Product id: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        System.out.println(services.facade().sales().createSale(Map.of(id, quantity), user.username()));
    }

    private void exportReport() {
        try {
            var report = new ReportDecorator(() -> {
                StringBuilder builder = new StringBuilder("Inventory Report\n");
                services.facade().products().list().forEach(product -> builder
                        .append(product.productId()).append(',')
                        .append(product.name()).append(',')
                        .append(product.stock()).append(',')
                        .append(product.sellingPrice()).append('\n'));
                return builder.toString();
            });
            Future<Path> future = services.facade().reports().submit(() ->
                    new CsvExporter().export(report.generate(), Path.of("exports", "inventory-report.csv")));
            System.out.println("Report exported to " + future.get());
        } catch (Exception ex) {
            System.out.println("Report failed: " + ex.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Products");
        System.out.println("2. Add product");
        System.out.println("3. Adjust stock");
        System.out.println("4. Dashboard");
        System.out.println("5. Low stock");
        System.out.println("6. Sale");
        System.out.println("7. Export inventory report");
        System.out.println("8. Notifications");
        System.out.println("0. Exit");
        System.out.print("> ");
    }

    private static String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
