package com.inventory.singleton;

import com.inventory.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Thread-safe singleton for JDBC connections.
 */
public final class ConnectionFactory {

    private static volatile ConnectionFactory instance;
    private final DatabaseConfig config;

    private ConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    /**
     * Returns the singleton instance.
     */
    public static ConnectionFactory getInstance() {
        ConnectionFactory local = instance;

        if (local == null) {
            synchronized (ConnectionFactory.class) {
                local = instance;

                if (local == null) {
                    instance = local = new ConnectionFactory(DatabaseConfig.load());
                }
            }
        }

        return local;
    }

    /**
     * Opens a new JDBC connection.
     */
    public Connection getConnection() throws SQLException {

        System.out.println("\n========== DATABASE CONNECTION ==========");
        System.out.println("Database URL : " + config.url());
        System.out.println("Username     : " + config.username());
        System.out.println("=========================================\n");

        Connection connection = DriverManager.getConnection(
                config.url(),
                config.username(),
                config.password()
        );

        System.out.println("✅ Successfully connected to MySQL.\n");

        return connection;
    }
}