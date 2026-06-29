package com.inventory.search;

import com.inventory.model.Product;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/** Maintains practical product indexes using core Java collections. */
public final class ProductIndex {
    private final Map<String, Product> byBarcode = new HashMap<>();
    private final Set<String> names = new HashSet<>();
    private final LinkedList<Product> recentlyViewed = new LinkedList<>();
    private final Queue<Product> reviewQueue = new ArrayDeque<>();
    private final Stack<Runnable> undoStack = new Stack<>();

    /** Rebuilds indexes from products. */
    public void rebuild(List<Product> products) {
        byBarcode.clear();
        names.clear();
        products.forEach(product -> {
            byBarcode.put(product.barcode(), product);
            names.add(product.name().toLowerCase());
            reviewQueue.offer(product);
        });
    }

    /** Finds by barcode and records recent access. */
    public Optional<Product> findByBarcode(String barcode) {
        Product product = byBarcode.get(barcode);
        if (product != null) {
            recentlyViewed.addFirst(product);
            undoStack.push(() -> recentlyViewed.remove(product));
        }
        return Optional.ofNullable(product);
    }

    /** Returns true if the name is already indexed. */
    public boolean containsName(String name) {
        return names.contains(name.toLowerCase());
    }

    /** Undoes the most recent index side effect. */
    public void undoLastAccess() {
        if (!undoStack.empty()) {
            undoStack.pop().run();
        }
    }
}
