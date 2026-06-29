package com.inventory.search;

import com.inventory.model.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

/** Search, sort, filter, binary-search, and ranking utilities for products. */
public final class ProductSearchEngine {
    /** Filters low-stock products. */
    public List<Product> lowStock(List<Product> products) {
        return products.stream().filter(Product::isLowStock).toList();
    }

    /** Sorts products by name, price, quantity, or date. */
    public List<Product> sort(List<Product> products, String field) {
        Comparator<Product> comparator = switch (field.toLowerCase()) {
            case "price" -> Comparator.comparing(Product::sellingPrice);
            case "quantity" -> Comparator.comparingInt(Product::stock);
            case "date" -> Comparator.comparing(Product::createdAt);
            default -> Comparator.naturalOrder();
        };
        return products.stream().sorted(comparator).toList();
    }

    /** Creates a category index using TreeMap. */
    public TreeMap<Integer, List<Product>> byCategory(List<Product> products) {
        TreeMap<Integer, List<Product>> index = new TreeMap<>();
        products.forEach(product -> index.computeIfAbsent(product.categoryId(), ignored -> new ArrayList<>()).add(product));
        return index;
    }

    /** Returns products with the lowest stock first using a priority queue. */
    public List<Product> priorityLowStock(List<Product> products) {
        PriorityQueue<Product> queue = new PriorityQueue<>(Comparator.comparingInt(Product::stock));
        queue.addAll(products);
        ArrayList<Product> ranked = new ArrayList<>();
        while (!queue.isEmpty()) {
            ranked.add(queue.poll());
        }
        return ranked;
    }

    /** Binary search by sorted product id. */
    public int binarySearchById(List<Product> products, int productId) {
        List<Product> sorted = products.stream().sorted(Comparator.comparingInt(Product::productId)).toList();
        int left = 0;
        int right = sorted.size() - 1;
        while (left <= right) {
            int middle = (left + right) >>> 1;
            int current = sorted.get(middle).productId();
            if (current == productId) {
                return middle;
            }
            if (current < productId) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }
        return -1;
    }
}
