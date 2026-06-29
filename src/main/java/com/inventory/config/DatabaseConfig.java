package com.inventory.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/** Loads database settings from system properties, environment variables, or application.properties. */
public final class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConfig(String url, String username, String password) {
        this.url = Objects.requireNonNull(url);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
    }

    /** Loads configuration with safe local defaults. */
    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException ignored) {
            // Defaults and environment variables remain available.
        }
        String url = first("DB_URL", properties.getProperty("db.url"),
                "jdbc:mysql://localhost:3306/inventory_management?useSSL=false&serverTimezone=UTC");
        String user = first("DB_USERNAME", properties.getProperty("db.username"), "root");
        String pass = first("DB_PASSWORD", properties.getProperty("db.password"), "");
        return new DatabaseConfig(url, user, pass);
    }

    private static String first(String env, String property, String fallback) {
        String value = System.getenv(env);
        if (value != null && !value.isBlank()) {
            return value;
        }
        return property == null || property.isBlank() ? fallback : property;
    }

    public String url() { return url; }
    public String username() { return username; }
    public String password() { return password; }
}
