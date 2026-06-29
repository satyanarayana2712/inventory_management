package com.inventory.service;

import com.inventory.exception.AuthorizationException;
import com.inventory.model.Role;
import com.inventory.model.User;
import java.util.EnumSet;
import java.util.Set;

/** Performs role-based authorization checks. */
public final class AuthorizationService {
    /** Requires one of the allowed roles. */
    public void require(User user, Role... roles) throws AuthorizationException {
        Set<Role> allowed = EnumSet.noneOf(Role.class);
        for (Role role : roles) {
            allowed.add(role);
        }
        if (user == null || !allowed.contains(user.role())) {
            throw new AuthorizationException("User is not authorized for this operation");
        }
    }
}
