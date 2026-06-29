package com.inventory.service;

import com.inventory.builder.ProductBuilder;
import com.inventory.dashboard.DashboardService;
import com.inventory.exception.InventoryException;
import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.multithreading.AuditLogService;
import com.inventory.multithreading.BackgroundScheduler;
import com.inventory.multithreading.NotificationService;
import com.inventory.multithreading.ReportExecutor;
import com.inventory.multithreading.StockMonitor;
import com.inventory.observer.EventPublisher;
import com.inventory.repository.InMemoryProductRepository;
import com.inventory.repository.InMemoryUserRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.search.ProductSearchEngine;
import com.inventory.util.PasswordHasher;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

/** Manual dependency-injection container for the application. */
public final class ApplicationServices {
    private final ProductRepository productRepository;
    private final InMemoryUserRepository userRepository;
    private final EventPublisher events;
    private final AuditLogService audit;
    private final NotificationService notifications;
    private final BackgroundScheduler scheduler;
    private final ReportExecutor reportExecutor;
    private final StockMonitor stockMonitor;
    private final InventoryFacade facade;
    private final AuthService authService;

    private ApplicationServices(ProductRepository productRepository, InMemoryUserRepository userRepository,
                                EventPublisher events, AuditLogService audit, NotificationService notifications,
                                BackgroundScheduler scheduler, ReportExecutor reportExecutor,
                                StockMonitor stockMonitor, InventoryFacade facade, AuthService authService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.events = events;
        this.audit = audit;
        this.notifications = notifications;
        this.scheduler = scheduler;
        this.reportExecutor = reportExecutor;
        this.stockMonitor = stockMonitor;
        this.facade = facade;
        this.authService = authService;
    }

    /** Creates a fully runnable demo application. */
    public static ApplicationServices createDemo() {
        InMemoryProductRepository products = new InMemoryProductRepository();
        InMemoryUserRepository users = new InMemoryUserRepository();
        EventPublisher events = new EventPublisher();
        AuditLogService audit = new AuditLogService(Path.of("logs", "audit.log"));
        NotificationService notifications = new NotificationService();
        events.subscribe(notifications);
        ProductService productService = new ProductService(products, events, audit);
        SalesService salesService = new SalesService(products, audit, events);
        PurchaseService purchaseService = new PurchaseService(productService, audit, events);
        DashboardService dashboard = new DashboardService(products);
        ProductSearchEngine search = new ProductSearchEngine();
        ReportExecutor reportExecutor = new ReportExecutor();
        StockMonitor stockMonitor = new StockMonitor(products, events);
        BackgroundScheduler scheduler = new BackgroundScheduler();
        InventoryFacade facade = new InventoryFacade(productService, salesService, purchaseService, dashboard, search, reportExecutor);
        AuthService auth = new AuthService(users);
        ApplicationServices services = new ApplicationServices(products, users, events, audit, notifications,
                scheduler, reportExecutor, stockMonitor, facade, auth);
        services.seed();
        scheduler.start();
        stockMonitor.start();
        return services;
    }

    private void seed() {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            userRepository.save(new User(1, "admin", PasswordHasher.hash("admin123".toCharArray()), Role.ADMIN, "admin@example.com"));
            productRepository.save(new ProductBuilder().name("USB Keyboard")
                    .prices(new BigDecimal("18.50"), new BigDecimal("29.99"))
                    .stock(25, 5).barcode("USBKEY1001").build());
            productRepository.save(new ProductBuilder().name("Wireless Mouse")
                    .prices(new BigDecimal("9.75"), new BigDecimal("19.99"))
                    .stock(4, 5).barcode("MOUSE1002").build());
        } catch (InventoryException ex) {
            throw new IllegalStateException("Unable to seed demo data", ex);
        } finally {
            latch.countDown();
        }
    }

    public InventoryFacade facade() { return facade; }
    public AuthService authService() { return authService; }
    public NotificationService notifications() { return notifications; }

    /** Stops background workers. */
    public void shutdown() {
        stockMonitor.close();
        scheduler.close();
        reportExecutor.close();
        notifications.close();
        audit.close();
    }
}
