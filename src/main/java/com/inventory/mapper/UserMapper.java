package com.inventory.mapper;

import com.inventory.model.Role;
import com.inventory.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Maps JDBC rows into users. */
public final class UserMapper {
    private UserMapper() {
    }

    /** Creates a user from the current result-set row. */
    public static User fromResultSet(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                Role.valueOf(rs.getString("role")), rs.getString("email"));
    }
}
