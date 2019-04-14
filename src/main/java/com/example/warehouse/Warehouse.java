package com.example.warehouse;

import com.example.Main;
import com.example.warehouse.dal.CustomerDao;
import com.example.warehouse.dal.InventoryDao;
import com.example.warehouse.dal.OrderDao;
import com.example.warehouse.dal.ProductDao;

import java.util.*;

import static java.util.stream.Collectors.*;

public final class Warehouse {

    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final InventoryDao inventoryDao;
    private final OrderDao orderDao;

    public Warehouse(
        ProductDao productDao,
        CustomerDao customerDao,
        InventoryDao inventoryDao,
        OrderDao orderDao) {
        this.productDao = productDao;
        this.customerDao = customerDao;
        this.inventoryDao = inventoryDao;
        this.orderDao = orderDao;
    }

    public Collection<Product> getProducts() throws WarehouseException {
        return productDao.getProducts()
            .stream()
            .sorted(Comparator.comparing(Product::getId))
            .collect(toUnmodifiableList());
    }

    public Collection<Customer> getCustomers() throws WarehouseException {
        return customerDao.getCustomers()
            .stream()
            .sorted(Comparator.comparing(Customer::getId))
            .collect(toUnmodifiableList());
    }

    public Collection<Order> getOrders() throws WarehouseException {
        return orderDao.getOrders()
            .stream()
            .sorted()
            .sorted(Comparator.comparing(Order::getId))
            .collect(toUnmodifiableList());
    }

    public void addProduct(String name, int price) throws WarehouseException {
        if (price < 0) {
            throw new IllegalArgumentException("The product's price cannot be negative.");
        }
        Product product = new Product(name, price);
        productDao.addProduct(product);
    }

    public void addOrder(int customerId, Map<Integer, Integer> quantities) throws WarehouseException {
        if (quantities.isEmpty()) {
            throw new IllegalArgumentException("There has to items in the order, it cannot be empty.");
        }
        Customer customer = customerDao.getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer ID: " + customerId);
        }
        Map<Product, Integer> mappedQuantities = new HashMap<>();
        for (var entry : quantities.entrySet()) {
            Product product = productDao.getProduct(entry.getKey());
            if (product == null) {
                throw new IllegalArgumentException("Unknown product ID: " + entry.getKey());
            }
            int quantity = entry.getValue();
            if (quantity < 1) {
                throw new IllegalArgumentException("Ordered quantity must be greater than 0.");
            }
            mappedQuantities.put(product, quantity);
        }
        inventoryDao.updateStock(mappedQuantities);
        Order order = new Order(customer, mappedQuantities);
        orderDao.addOrder(order);
    }

    public Report generateReport(Report.Type type) throws WarehouseException {
        if (type == Report.Type.DAILY_REVENUE) {
            if (Main.CLIENT_ID == 1) {
                return generateDailyRevenueReport();
            } else if (Main.CLIENT_ID == 2) {
                return generateDailyRevenueReport2();
            } else {
                throw new IllegalStateException("Unknown client ID: " + Main.CLIENT_ID);
            }
        }
        throw new UnsupportedOperationException(String.format("Report type: %s not yet implemented.", type));
    }

    private Report generateDailyRevenueReport() throws WarehouseException {
        Report report = new Report();
        report.addLabel("Date");
        report.addLabel("Total revenue");
        orderDao.getOrders()
            .stream()
            .filter(o -> !o.isPending())
            .sorted()
            .collect(groupingBy(Order::getDate, LinkedHashMap::new, summingInt(Order::getTotalPrice)))
            .forEach((date, totalRevenue) -> report.addRecord(Arrays.asList(date, totalRevenue)));
        return report;
    }

    private Report generateDailyRevenueReport2() throws WarehouseException {
        Report report = new Report();
        report.addLabel("Date");
        report.addLabel("Total products");
        report.addLabel("Total revenue");
        orderDao.getOrders()
            .stream()
            .sorted()
            .collect(groupingBy(Order::getDate, LinkedHashMap::new, toList()))
            .forEach((date, orders) -> report.addRecord(Arrays.asList(
                date,
                orders
                    .stream()
                    .sorted()
                    .map(Order::getQuantities)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .mapToInt(Integer::intValue)
                    .sum(),
                orders
                    .stream()
                    .sorted()
                    .mapToInt(Order::getTotalPrice)
                    .sum())));
        return report;
    }
}
