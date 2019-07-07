package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.Order;
import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;
import com.example.warehouse.util.CsvReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public final class MemoryOrderDao implements OrderDao {

    private static class OrderDaoHolder {
        private static final OrderDao INSTANCE = new MemoryOrderDao();
    }

    private static final String DEFAULT_ORDERS_CSV_FILE = "orders.csv";

    public static OrderDao getInstance() {
        return OrderDaoHolder.INSTANCE;
    }

    private final List<Order> orders;

    private MemoryOrderDao() {
        this.orders = new ArrayList<>();
        try {
            readOrders();
        } catch (FileNotFoundException ex) {
            System.err.println("Please ensure the required CSV files are present: " + ex.getMessage());
            System.exit(1);
        } catch (WarehouseException ex) {
            System.err.println("Failed to initialize the warehouse: " + ex.getMessage());
            System.exit(2);
        }
    }

    @Override
    public Collection<Order> getOrders() {
        return orders.stream()
            .map(Order::new)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public synchronized void addOrder(Order order) {
        int max = orders.stream()
            .mapToInt(Order::getId)
            .max()
            .orElseThrow();
        int id = max + 1;
        order.setId(id);
        orders.add(order);
    }

    private void readOrders() throws FileNotFoundException, WarehouseException {
        CsvReader reader = new CsvReader(new FileInputStream(DEFAULT_ORDERS_CSV_FILE));
        while (reader.hasNextRow()) {
            List<String> row = reader.nextRow();
            if (row.isEmpty()) {
                continue;
            }
            int id;
            try {
                id = Integer.valueOf(row.get(0));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read orders: invalid order ID in CSV, must be an integer.", ex);
            }
            int customerId;
            try {
                customerId = Integer.valueOf(row.get(1));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read orders: invalid customer ID in CSV, must be an integer.", ex);
            }
            Customer customer = MemoryCustomerDao.getInstance().getCustomer(customerId);
            if (customer == null) {
                throw new WarehouseException("Failed to read orders: unknown customer ID in CSV.");
            }
            LocalDate date;
            try {
                date = LocalDate.parse(row.get(2));
            } catch (DateTimeParseException ex) {
                throw new WarehouseException("Failed to read orders: invalid date in CSV, format must be `yyyy-MM-dd'.", ex);
            }
            boolean pending = Boolean.valueOf(row.get(3));
            Map<Product, Integer> quantities = new HashMap<>();
            for (String orderDetail : row.subList(4, row.size())) {
                String[] productIdAndQuantity = orderDetail.split("x");
                int productId;
                try {
                    productId = Integer.valueOf(productIdAndQuantity[0]);
                } catch (NumberFormatException ex) {
                    throw new WarehouseException("Failed to read orders: invalid product ID in CSV, must be an integer.", ex);
                }
                Product product = MemoryProductDao.getInstance().getProduct(productId);
                if (product == null) {
                    throw new WarehouseException("Failed to read orders: unknown product ID in CSV.");
                }
                int quantity;
                try {
                    quantity = Integer.valueOf(productIdAndQuantity[1]);
                } catch (NumberFormatException ex) {
                    throw new WarehouseException("Failed to read orders: invalid quantity in CSV, must be an integer.", ex);
                }
                quantities.put(product, quantity);
            }
            orders.add(new Order(id, customer, date, quantities, pending));
        }
    }
}
