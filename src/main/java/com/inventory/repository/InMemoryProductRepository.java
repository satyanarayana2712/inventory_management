package com.inventory.repository;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Thread-safe in-memory repository used for demo mode and tests. */
public final class InMemoryProductRepository implements ProductRepository {
    private final ConcurrentHashMap<Integer, Product> products = new ConcurrentHashMap<>();
    private final AtomicInteger ids = new AtomicInteger(1000);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Product save(Product entity) throws InventoryException {
        lock.writeLock().lock();
        try {
            int id = entity.productId() > 0 ? entity.productId() : ids.incrementAndGet();
            Product saved = new Product(id, entity.name(), entity.description(), entity.categoryId(), entity.supplierId(),
                    entity.purchasePrice(), entity.sellingPrice(), entity.stock(), entity.minimumStock(),
                    entity.barcode(), entity.createdAt());
            products.put(id, saved);
            return saved;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Product> findById(Integer id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(products.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findAll() {
        lock.readLock().lock();
        try {
            ArrayList<Product> list = new ArrayList<>(products.values());
            list.sort(Comparator.naturalOrder());
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteById(Integer id) {
        products.remove(id);
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        return products.values().stream().filter(p -> p.barcode().equalsIgnoreCase(barcode)).findFirst();
    }

    @Override
    public List<Product> findByName(String term) {
        return products.values().stream()
                .filter(p -> p.name().toLowerCase().contains(term.toLowerCase()))
                .sorted()
                .toList();
    }

    @Override
    public void updateStock(int productId, int newStock) throws InventoryException {
        Product product = findById(productId).orElseThrow(() -> new InventoryException("Product not found"));
        save(product.withStock(newStock));
    }
}
