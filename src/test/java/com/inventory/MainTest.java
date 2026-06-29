package com.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Smoke tests for the application entry point.
 */
class MainTest {

    @Test
    void mainClassLoads() {
        assertDoesNotThrow(() -> Class.forName(Main.class.getName()));
    }
}
