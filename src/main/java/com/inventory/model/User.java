package com.inventory.model;

/** Authenticated application user. */
public record User(int id, String username, String passwordHash, Role role, String email) {
}
