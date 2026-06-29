package com.inventory.repository;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import java.util.List;
import java.util.Optional;

/** Product repository contract. */
public interface ProductRepository extends CrudRepository<Product, Integer> {
    /** Finds a product by barcode. */
    Optional<Product> findByBarcode(String barcode) throws InventoryException;

    /** Finds products whose names contain the term. */
    List<Product> findByName(String term) throws InventoryException;

    /** Updates stock atomically. */
    void updateStock(int productId, int newStock) throws InventoryException;
}
