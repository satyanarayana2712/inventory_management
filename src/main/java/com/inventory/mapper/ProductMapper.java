package com.inventory.mapper;

import com.inventory.model.Product;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Maps JDBC rows into products. */
public final class ProductMapper {
    private ProductMapper() {
    }

    /** Creates a product from the current result-set row. */
    public static Product fromResultSet(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("category_id"),
                rs.getInt("supplier_id"),
                rs.getBigDecimal("purchase_price"),
                rs.getBigDecimal("selling_price"),
                rs.getInt("stock"),
                rs.getInt("minimum_stock"),
                rs.getString("barcode"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }
}
