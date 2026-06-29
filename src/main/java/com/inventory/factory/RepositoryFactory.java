package com.inventory.factory;

import com.inventory.repository.InMemoryProductRepository;
import com.inventory.repository.JdbcProductRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.singleton.ConnectionFactory;

/** Factory for repository implementations. */
public final class RepositoryFactory {
    private RepositoryFactory() {
    }

    /** Creates a product repository by profile. */
    public static ProductRepository productRepository(String profile) {
        return "mysql".equalsIgnoreCase(profile)
                ? new JdbcProductRepository(ConnectionFactory.getInstance())
                : new InMemoryProductRepository();
    }
}
