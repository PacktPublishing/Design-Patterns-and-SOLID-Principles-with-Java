package com.example.warehouse;

import com.example.warehouse.dal.CustomerDao;
import com.example.warehouse.dal.InventoryDao;
import com.example.warehouse.dal.OrderDao;
import com.example.warehouse.dal.ProductDao;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public final class Warehouse {

    private static class WarehouseHolder {
        private static final Warehouse INSTANCE = new Warehouse();
    }

    public static Warehouse getInstance() {
        return WarehouseHolder.INSTANCE;
    }

    private Warehouse() {
    }

    public Collection<Product> getProducts() {
        return ProductDao.getInstance().getProducts()
            .stream()
            .sorted(Comparator.comparing(Product::getId))
            .collect(Collectors.toUnmodifiableList());
    }

    public Collection<Customer> getCustomers() {
        return CustomerDao.getInstance().getCustomers()
            .stream()
            .sorted(Comparator.comparing(Customer::getId))
            .collect(Collectors.toUnmodifiableList());
    }

    public Collection<Order> getOrders() {
        return OrderDao.getInstance().getOrders()
            .stream()
            .sorted()
            .sorted(Comparator.comparing(Order::getId))
            .collect(Collectors.toUnmodifiableList());
    }

    public void addProduct(String name, int price) {
        if (price < 0) {
            throw new IllegalArgumentException("The product's price cannot be negative.");
        }
        Product product = new Product(name, price);
        ProductDao.getInstance().addProduct(product);
    }

    public void addOrder(int customerId, Map<Integer, Integer> quantities) {
        if (quantities.isEmpty()) {
            throw new IllegalArgumentException("There has to items in the order, it cannot be empty.");
        }
        Customer customer = CustomerDao.getInstance().getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer ID: " + customerId);
        }
        Map<Product, Integer> mappedQuantities = new HashMap<>();
        for (var entry : quantities.entrySet()) {
            Product product = ProductDao.getInstance().getProduct(entry.getKey());
            if (product == null) {
                throw new IllegalArgumentException("Unknown product ID: " + entry.getKey());
            }
            int quantity = entry.getValue();
            if (quantity < 1) {
                throw new IllegalArgumentException("Ordered quantity must be greater than 0.");
            }
            mappedQuantities.put(product, quantity);
        }
        InventoryDao.getInstance().updateStock(mappedQuantities);
        Order order = new Order(customer, mappedQuantities);
        OrderDao.getInstance().addOrder(order);
    }

    public Report generateReport(Report.Type type) {
        if (type == Report.Type.DAILY_REVENUE) {
            return generateDailyRevenueReport();
        }
        throw new UnsupportedOperationException(String.format("Report type: %s not yet implemented.", type));
    }

    private Report generateDailyRevenueReport() {
        Report report = new Report();
        report.addLabel("Date");
        report.addLabel("Total revenue");
        OrderDao.getInstance().getOrders()
            .stream()
            .filter(o -> !o.isPending())
            .sorted()
            .collect(groupingBy(Order::getDate, LinkedHashMap::new, summingInt(Order::getTotalPrice)))
            .forEach((date, totalRevenue) -> report.addRecord(Arrays.asList(date, totalRevenue)));
        return report;
    }
}
