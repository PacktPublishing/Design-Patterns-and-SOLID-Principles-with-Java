package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.WarehouseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DbCustomerDao extends AbstractDbDao implements CustomerDao {

    public DbCustomerDao() {
    }

    @Override
    public Collection<Customer> getCustomers() throws WarehouseException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            List<Customer> customers = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery("SELECT * FROM customers")) {
                while (rs.next()) {
                    customers.add(toCustomer(rs));
                }
            }
            return customers;
        } catch (SQLException ex) {
            throw new WarehouseException("Trouble while fetching customers.", ex);
        }
    }

    @Override
    public Customer getCustomer(int id) throws WarehouseException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return toCustomer(rs);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new WarehouseException(String.format("Trouble while fetching customer (%s).", id), ex);
        }
    }

    @Override
    public void deleteCustomer(int id) throws WarehouseException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            statement.setInt(1, id);
            if (statement.executeUpdate() != 1) {
                throw new SQLException(String.format("Expected one customer with ID %s to be deleted.", id));
            }
        } catch (SQLException ex) {
            throw new WarehouseException(String.format("Trouble while fetching customer (%s).", id), ex);
        }
    }

    private Customer toCustomer(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Customer(id, name);
    }
}
