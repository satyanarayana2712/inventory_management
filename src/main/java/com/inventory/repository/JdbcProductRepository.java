package com.inventory.repository;

import com.inventory.exception.InventoryException;
import com.inventory.mapper.ProductMapper;
import com.inventory.model.Product;
import com.inventory.singleton.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** MySQL JDBC implementation of product persistence. */
public final class JdbcProductRepository implements ProductRepository {
    private final ConnectionFactory connectionFactory;

    /** Creates a repository with the shared connection factory. */
    public JdbcProductRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Product save(Product entity) throws InventoryException {
        if (entity.productId() > 0) {
            update(entity);
            return entity;
        }
        String sql = """
                INSERT INTO products(name, description, category_id, supplier_id, purchase_price,
                selling_price, stock, minimum_stock, barcode, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindProduct(statement, entity);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1)).orElse(entity);
                }
                return entity;
            }
        } catch (SQLException ex) {
            throw new InventoryException("Unable to save product", ex);
        }
    }

    private void update(Product entity) throws InventoryException {
        String sql = """
                UPDATE products SET name=?, description=?, category_id=?, supplier_id=?, purchase_price=?,
                selling_price=?, stock=?, minimum_stock=?, barcode=?, created_at=? WHERE product_id=?
                """;
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindProduct(statement, entity);
            statement.setInt(11, entity.productId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new InventoryException("Unable to update product", ex);
        }
    }

    private static void bindProduct(PreparedStatement statement, Product entity) throws SQLException {
        statement.setString(1, entity.name());
        statement.setString(2, entity.description());
        statement.setInt(3, entity.categoryId());
        statement.setInt(4, entity.supplierId());
        statement.setBigDecimal(5, entity.purchasePrice());
        statement.setBigDecimal(6, entity.sellingPrice());
        statement.setInt(7, entity.stock());
        statement.setInt(8, entity.minimumStock());
        statement.setString(9, entity.barcode());
        statement.setTimestamp(10, Timestamp.valueOf(entity.createdAt()));
    }

    @Override
    public Optional<Product> findById(Integer id) throws InventoryException {
        return queryOne("SELECT * FROM products WHERE product_id = ?", id);
    }

    @Override
    public List<Product> findAll() throws InventoryException {
        String sql = "SELECT * FROM products ORDER BY name";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(ProductMapper.fromResultSet(rs));
            }
            return products;
        } catch (SQLException ex) {
            throw new InventoryException("Unable to load products", ex);
        }
    }

    @Override
    public void deleteById(Integer id) throws InventoryException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new InventoryException("Unable to delete product", ex);
        }
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) throws InventoryException {
        return queryOne("SELECT * FROM products WHERE barcode = ?", barcode);
    }

    @Override
    public List<Product> findByName(String term) throws InventoryException {
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + term + "%");
            try (ResultSet rs = statement.executeQuery()) {
                List<Product> products = new ArrayList<>();
                while (rs.next()) {
                    products.add(ProductMapper.fromResultSet(rs));
                }
                return products;
            }
        } catch (SQLException ex) {
            throw new InventoryException("Unable to search products", ex);
        }
    }

    @Override
    public void updateStock(int productId, int newStock) throws InventoryException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE products SET stock = ? WHERE product_id = ?")) {
            statement.setInt(1, newStock);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new InventoryException("Unable to update stock", ex);
        }
    }

    private Optional<Product> queryOne(String sql, Object value) throws InventoryException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (value instanceof Integer id) {
                statement.setInt(1, id);
            } else {
                statement.setString(1, String.valueOf(value));
            }
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(ProductMapper.fromResultSet(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new InventoryException("Unable to query product", ex);
        }
    }
}
