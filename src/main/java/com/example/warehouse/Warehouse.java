package com.example.warehouse;

import com.example.warehouse.util.CsvReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public final class Warehouse {

    private static class WarehouseHolder {
        private static final Warehouse INSTANCE = new Warehouse();
    }

    private static final String DEFAULT_PRODUCTS_CSV_FILE = "products.csv";
    private static final String DEFAULT_INVENTORY_CSV_FILE = "inventory.csv";
    private static final String DEFAULT_CUSTOMERS_CSV_FILE = "customers.csv";
    private static final String DEFAULT_ORDERS_CSV_FILE = "orders.csv";

    public static Warehouse getInstance() {
        return WarehouseHolder.INSTANCE;
    }

    private final Map<Integer, Product> products;
    private final Map<Integer, Integer> inventory;
    private final Map<Integer, Customer> customers;
    private final List<Order> orders;

    private Warehouse() {
        this.products = new HashMap<>();
        this.inventory = new HashMap<>();
        this.customers = new HashMap<>();
        this.orders = new ArrayList<>();

        try {
            readProducts();
            readInventory();
            readCustomers();
            readOrders();
        } catch (FileNotFoundException ex) {
            System.err.println("Please ensure the required CSV files are present: " + ex.getMessage());
            System.exit(1);
        } catch (WarehouseException ex) {
            System.err.println("Failed to initialize the warehouse: " + ex.getMessage());
            System.exit(2);
        }
    }

    public Collection<Product> getProducts() {
        return products
            .values()
            .stream()
            .sorted(Comparator.comparing(Product::getId))
            .collect(Collectors.toList());
    }

    public Collection<Customer> getCustomers() {
        return customers
            .values()
            .stream()
            .sorted(Comparator.comparing(Customer::getId))
            .collect(Collectors.toList());
    }

    public Collection<Order> getOrders() {
        return orders
            .stream()
            .sorted()
            .sorted(Comparator.comparing(Order::getId))
            .collect(Collectors.toList());
    }

    public synchronized void addProduct(String name, int price) {
        if (price < 0) {
            throw new IllegalArgumentException("The product's price cannot be negative.");
        }
        int max = Collections.max(products.keySet());
        int id = max + 1;
        Product product = new Product(id, name, price);
        products.put(id, product);
    }

    public synchronized void addOrder(int customerId, Map<Integer, Integer> quantities) {
        if (quantities.isEmpty()) {
            throw new IllegalArgumentException("There has to items in the order, it cannot be empty.");
        }
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer ID: " + customerId);
        }
        Map<Product, Integer> mappedQuantities = new HashMap<>();
        for (var entry : quantities.entrySet()) {
            Product product = products.get(entry.getKey());
            if (product == null) {
                throw new IllegalArgumentException("Unknown product ID: " + entry.getKey());
            }
            int quantity = entry.getValue();
            if (quantity < 1) {
                throw new IllegalArgumentException("Ordered quantity must be greater than 0.");
            }
            if (!inventory.containsKey(product.getId())) {
                throw new IllegalArgumentException(String.format("Product (%s) not in stock.", product.getId()));
            }
            int stock = inventory.get(product.getId());
            if (stock - quantity < 0) {
                throw new IllegalArgumentException(
                    String.format("Not enough product (%s) in stock. Available %s. Ordered %s.", product.getId(), stock, quantity));
            }
            inventory.put(product.getId(), stock - quantity);
            mappedQuantities.put(product, quantity);
        }
        int max = Collections.max(products.keySet());
        int id = max + 1;
        Order order = new Order(id, customer, LocalDate.now(), mappedQuantities, true);
        orders.add(order);
    }

    public Report generateDailyRevenueReport(Report.Type type) {
        if (type == Report.Type.DAILY_REVENUE) {
            return generateDailyRevenueReport();
        }
        throw new UnsupportedOperationException(String.format("Report type: %s not yet implemented.", type));
    }

    private Report generateDailyRevenueReport() {
        Report report = new Report();
        report.addLabel("Date");
        report.addLabel("Total revenue");
        orders.stream()
            .filter(o -> !o.isPending())
            .sorted()
            .collect(groupingBy(Order::getDate, LinkedHashMap::new, summingInt(Order::getTotalPrice)))
            .forEach((date, totalRevenue) -> report.addRecord(Arrays.asList(date, totalRevenue)));
        return report;
    }

    private void readProducts() throws FileNotFoundException, WarehouseException {
        CsvReader reader = new CsvReader(new FileInputStream(DEFAULT_PRODUCTS_CSV_FILE));
        while (reader.hasNextRow()) {
            List<String> row = reader.nextRow();
            if (row.isEmpty()) {
                continue;
            }
            int id;
            try {
                id = Integer.valueOf(row.get(0));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read products: invalid product ID in CSV, must be an integer.", ex);
            }
            String name = row.get(1);
            int price;
            try {
                price = Integer.valueOf(row.get(2));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read products: invalid price in CSV, must be an integer.", ex);
            }
            if (products.containsKey(id)) {
                throw new WarehouseException("Failed to read products: duplicate product ID in CSV.");
            }
            products.put(id, new Product(id, name, price));
        }
    }

    private void readInventory() throws FileNotFoundException, WarehouseException {
        CsvReader reader = new CsvReader(new FileInputStream(DEFAULT_INVENTORY_CSV_FILE));
        while (reader.hasNextRow()) {
            List<String> row = reader.nextRow();
            if (row.isEmpty()) {
                continue;
            }
            int id;
            try {
                id = Integer.valueOf(row.get(0));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read inventory: invalid product ID in CSV, must be an integer.", ex);
            }
            if (!products.containsKey(id)) {
                throw new WarehouseException("Failed to read inventory: unknown product ID: " + id);
            }
            int quantity;
            try {
                quantity = Integer.valueOf(row.get(1));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read inventory: invalid quantity in CSV, must be an integer.", ex);
            }
            inventory.put(id, quantity);
        }
    }

    private void readCustomers() throws FileNotFoundException, WarehouseException {
        CsvReader reader = new CsvReader(new FileInputStream(DEFAULT_CUSTOMERS_CSV_FILE));
        while (reader.hasNextRow()) {
            List<String> row = reader.nextRow();
            if (row.isEmpty()) {
                continue;
            }
            int id;
            try {
                id = Integer.valueOf(row.get(0));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read customers: invalid customer ID in CSV, must be an integer.", ex);
            }
            String name = row.get(1);
            customers.put(id, new Customer(id, name));
        }
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
            Customer customer = customers.get(customerId);
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
                Product product = products.get(productId);
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
