package com.inventory.repository;

import com.inventory.exception.InventoryException;
import java.util.List;
import java.util.Optional;

/** Generic repository abstraction. */
public interface CrudRepository<T, ID> {
    /** Saves an entity. */
    T save(T entity) throws InventoryException;

    /** Finds an entity by id. */
    Optional<T> findById(ID id) throws InventoryException;

    /** Returns all entities. */
    List<T> findAll() throws InventoryException;

    /** Deletes an entity by id. */
    void deleteById(ID id) throws InventoryException;
}
