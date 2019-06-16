package com.example.warehouse;

import com.example.warehouse.dal.CustomerDao;
import com.example.warehouse.dal.InventoryDao;
import com.example.warehouse.dal.OrderDao;
import com.example.warehouse.dal.ProductDao;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableList;

public final class Warehouse {

    private static final String EXTERNAL_CUSTOMERS_URL = System.getProperty("EXTERNAL_CUSTOMERS_URL", "http://localhost:3000/customers");

    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final InventoryDao inventoryDao;
    private final OrderDao orderDao;

    private final ReportGeneration reportGeneration;

    public Warehouse(
        ProductDao productDao,
        CustomerDao customerDao,
        InventoryDao inventoryDao,
        OrderDao orderDao,
        ReportGeneration reportGeneration) {
        this.productDao = productDao;
        this.customerDao = customerDao;
        this.inventoryDao = inventoryDao;
        this.orderDao = orderDao;
        this.reportGeneration = reportGeneration;
    }

    public Collection<Product> getProducts() throws WarehouseException {
        return productDao.getProducts()
            .stream()
            .sorted(Comparator.comparing(Product::getId))
            .collect(toUnmodifiableList());
    }

    public Collection<Customer> getCustomers() throws WarehouseException {
        Map<Integer, JSONObject> externalCustomers = fetchExternalCustomers();
        return customerDao.getCustomers()
            .stream()
            .map(c -> {
                JSONObject customer = externalCustomers.get(c.getId());
                JSONObject address = customer.getJSONObject("address");
                return new Customer(
                    c.getId(),
                    c.getName(),
                    LocalDate.parse(customer.getString("date_of_birth")),
                    customer.getString("company"),
                    customer.getString("phone"),
                    address.getString("street_address"),
                    address.getString("city"),
                    address.getString("state"),
                    address.getInt("zip_code"));
            })
            .sorted(Comparator.comparing(Customer::getId))
            .collect(toUnmodifiableList());
    }

    private Map<Integer, JSONObject> fetchExternalCustomers() throws WarehouseException {
        Map<Integer, JSONObject> results = new HashMap<>();
        try {
            URL url = new URL(EXTERNAL_CUSTOMERS_URL);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(500);
            try (InputStream is = connection.getInputStream()) {
                JSONArray customers = new JSONArray(new String(is.readAllBytes()));
                for (int i = 0; i < customers.length(); i++) {
                    JSONObject customer = customers.getJSONObject(i);
                    results.put(customer.getInt("id"), customer);
                }
            }
        } catch (IOException ex) {
            throw new WarehouseException(format("Problem while fetching additional customer data: %s", ex.getMessage()), ex);
        }
        return results;
    }

    public Collection<Order> getOrders() throws WarehouseException {
        return orderDao.getOrders()
            .stream()
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
        // TODO: updating stock and adding order should be atomic.
        orderDao.addOrder(order);
    }

    public Report generateReport(Report.Type type) throws WarehouseException {
        return reportGeneration.generateReport(type);
    }
}
