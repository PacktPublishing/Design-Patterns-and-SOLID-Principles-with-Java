package com.example.warehouse.dal;

import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DbProductDao extends AbstractDbDao implements ProductDao {

    public DbProductDao() {
    }

    @Override
    public Collection<Product> getProducts() throws WarehouseException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            List<Product> products = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery("SELECT * FROM products")) {
                while (rs.next()) {
                    products.add(toProduct(rs));
                }
            }
            return products;
        } catch (SQLException ex) {
            throw new WarehouseException("Trouble while fetching products.", ex);
        }
    }

    @Override
    public Product getProduct(int id) throws WarehouseException {
        try (Connection connection = getConnection();
                      PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return toProduct(rs);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new WarehouseException(String.format("Trouble while fetching product (%s).", id), ex);
        }
    }

    @Override
    public void addProduct(Product product) throws WarehouseException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO products(name, price) VALUES (?, ?)")) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getPrice());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new WarehouseException(String.format(
                "Trouble while adding product (%s, %s).", product.getName(), product.getPrice()), ex);
        }
    }

    private Product toProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int price = rs.getInt("price");
        return new Product(id, name, price);
    }
}
