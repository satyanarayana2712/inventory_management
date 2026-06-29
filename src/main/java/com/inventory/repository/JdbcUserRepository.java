package com.inventory.repository;

import com.inventory.exception.InventoryException;
import com.inventory.mapper.UserMapper;
import com.inventory.model.User;
import com.inventory.singleton.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** MySQL JDBC implementation of user persistence. */
public final class JdbcUserRepository implements UserRepository {
    private final ConnectionFactory connectionFactory;

    /** Creates a repository. */
    public JdbcUserRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public User save(User entity) throws InventoryException {
        String sql = "INSERT INTO users(username, password, role, email) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.username());
            statement.setString(2, entity.passwordHash());
            statement.setString(3, entity.role().name());
            statement.setString(4, entity.email());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? new User(keys.getInt(1), entity.username(), entity.passwordHash(), entity.role(), entity.email()) : entity;
            }
        } catch (SQLException ex) {
            throw new InventoryException("Unable to save user", ex);
        }
    }

    @Override
    public Optional<User> findById(Integer id) throws InventoryException {
        return queryOne("SELECT * FROM users WHERE id = ?", id);
    }

    @Override
    public List<User> findAll() throws InventoryException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users ORDER BY username");
             ResultSet rs = statement.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(UserMapper.fromResultSet(rs));
            }
            return users;
        } catch (SQLException ex) {
            throw new InventoryException("Unable to load users", ex);
        }
    }

    @Override
    public void deleteById(Integer id) throws InventoryException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new InventoryException("Unable to delete user", ex);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) throws InventoryException {
        return queryOne("SELECT * FROM users WHERE username = ?", username);
    }

    private Optional<User> queryOne(String sql, Object value) throws InventoryException {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (value instanceof Integer id) {
                statement.setInt(1, id);
            } else {
                statement.setString(1, String.valueOf(value));
            }
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(UserMapper.fromResultSet(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new InventoryException("Unable to query user", ex);
        }
    }
}
