package com.inventory.service;

import com.inventory.exception.InventoryException;
import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import com.inventory.util.PasswordHasher;
import java.util.Arrays;
import java.util.Optional;

/** Handles login and password validation. */
public final class AuthService {
    private final UserRepository users;

    /** Creates an authentication service. */
    public AuthService(UserRepository users) {
        this.users = users;
    }

    /** Attempts to authenticate a user. */
    public Optional<User> login(String username, char[] password) throws InventoryException {
        try {
            return users.findByUsername(username)
                    .filter(user -> PasswordHasher.verify(password, user.passwordHash()));
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
