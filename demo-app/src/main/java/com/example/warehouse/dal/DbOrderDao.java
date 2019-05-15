package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.Order;
import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DbOrderDao extends AbstractDbDao implements OrderDao {

    private static class OrderRecord {
        int orderId;
        LocalDate orderDate;
        boolean pending;
        int customerId;
        String customerName;
        int productId;
        String productName;
        int price;
        int quantity;

        OrderRecord(int orderId, LocalDate orderDate, boolean pending, int customerId, String customerName, int productId, String productName, int price, int quantity) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.pending = pending;
            this.customerId = customerId;
            this.customerName = customerName;
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        int getOrderId() {
            return orderId;
        }

        LocalDate getOrderDate() {
            return orderDate;
        }

        boolean isPending() {
            return pending;
        }

        int getCustomerId() {
            return customerId;
        }

        String getCustomerName() {
            return customerName;
        }

        int getProductId() {
            return productId;
        }

        String getProductName() {
            return productName;
        }

        int getPrice() {
            return price;
        }

        int getQuantity() {
            return quantity;
        }
    }

    public DbOrderDao() {
    }

    @Override
    public Collection<Order> getOrders() throws WarehouseException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            List<OrderRecord> results = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery(
                "SELECT "
                    + "o.id AS order_id, "
                    + "o.order_date, "
                    + "o.pending, "
                    + "c.id AS customer_id, "
                    + "c.name AS customer_name, "
                    + "p.id AS product_id, "
                    + "p.name AS product_name, "
                    + "p.price, "
                    + "od.quantity " +
                    "FROM orders AS o " +
                    "JOIN order_details AS od ON o.id = od.order_id " +
                    "JOIN products AS p ON p.id = od.product_id " +
                    "JOIN customers AS c ON c.id = o.customer_id " +
                    "GROUP BY o.id, p.id")) {
                while (rs.next()) {
                    results.add(new OrderRecord(
                        rs.getInt("order_id"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getBoolean("pending"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("price"),
                        rs.getInt("quantity")
                    ));
                }
                List<Order> orders = new ArrayList<>();
                for (var group : results.stream().collect(Collectors.groupingBy(OrderRecord::getOrderId)).entrySet()) {
                    int orderId = group.getKey();
                    List<OrderRecord> records = group.getValue();
                    OrderRecord firstRecord = records.get(0);

                    Map<Product, Integer> quantities = records.stream()
                        .collect(Collectors.groupingBy(
                            or -> new Product(or.productId, or.productName, or.price),
                            Collectors.summingInt(OrderRecord::getQuantity)));

                    orders.add(new Order(
                        orderId,
                        new Customer(firstRecord.customerId, firstRecord.customerName),
                        firstRecord.orderDate,
                        quantities,
                        firstRecord.pending));
                }
                return orders;
            }
        } catch (SQLException ex) {
            throw new WarehouseException("Trouble while fetching orders.", ex);
        }
    }

    @Override
    public void addOrder(Order order) throws WarehouseException {
        try (Connection connection = getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement orderStatement = connection.prepareStatement(
                "INSERT INTO orders(customer_id, order_date, pending) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                orderStatement.setInt(1, order.getCustomer().getId());
                orderStatement.setDate(2, Date.valueOf(order.getDate()));
                orderStatement.setBoolean(3, order.isPending());
                orderStatement.executeUpdate();
                ResultSet rs = orderStatement.getGeneratedKeys();
                int orderId;
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Problem inserting order.");
                }
                try (PreparedStatement orderDetailStatement = connection.prepareStatement(
                    "INSERT INTO order_details VALUES (?, ?, ?)")) {
                    for (var entry : order.getQuantities().entrySet()) {
                        Product product = entry.getKey();
                        int quantity = entry.getValue();
                        orderDetailStatement.setInt(1, orderId);
                        orderDetailStatement.setInt(2, product.getId());
                        orderDetailStatement.setInt(3, quantity);
                        orderDetailStatement.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException ex) {
            throw new WarehouseException("Trouble while adding order.", ex);
        }
    }
}
