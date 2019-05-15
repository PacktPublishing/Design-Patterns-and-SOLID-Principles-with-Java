package com.example.warehouse.dal;

import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class DbInventoryDao extends AbstractDbDao implements InventoryDao {

    public DbInventoryDao() {
    }

    @Override
    public void updateStock(Map<Product, Integer> quantities) throws WarehouseException {
        try (Connection connection = getConnection()) {
            for (var entry : quantities.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                int stock = getStockOrDefault(connection, product.getId(), 0);
                if (stock == 0) {
                    throw new IllegalArgumentException(String.format("Product (%s) not in stock.", product.getId()));
                }
                if (stock - quantity < 0) {
                    throw new IllegalArgumentException(
                        String.format("Not enough product (%s) in stock. Available %s. Ordered %s.", product.getId(), stock, quantity));
                }
            }
            for (var entry : quantities.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                int stock = getStockOrDefault(connection, product.getId(), 0);
                setStock(connection, product.getId(), stock - quantity);
            }
        } catch (SQLException ex) {
            throw new WarehouseException("Trouble while fetching inventory.", ex);
        }
    }

    private int getStockOrDefault(Connection connection, int productId, int defaultQuantity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM inventory WHERE product_id = ?")) {
            statement.setInt(1, productId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
                return defaultQuantity;
            }
        }
    }

    private void setStock(Connection connection, int productId, int quantity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "UPDATE inventory SET quantity = ? WHERE product_id = ?")) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
    }
}
