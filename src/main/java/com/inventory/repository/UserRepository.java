package com.inventory.repository;

import com.inventory.exception.InventoryException;
import com.inventory.model.User;
import java.util.Optional;

/** User persistence contract. */
public interface UserRepository extends CrudRepository<User, Integer> {
    /** Finds a user by username. */
    Optional<User> findByUsername(String username) throws InventoryException;
}
