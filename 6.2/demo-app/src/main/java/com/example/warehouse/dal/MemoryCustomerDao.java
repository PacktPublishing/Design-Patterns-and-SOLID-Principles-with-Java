package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.WarehouseException;
import com.example.warehouse.util.CsvReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MemoryCustomerDao implements CustomerDao {

    private static final String DEFAULT_CUSTOMERS_CSV_FILE = "customers.csv";

    private final Map<Integer, Customer> customers;

    public MemoryCustomerDao() {
        this.customers = new HashMap<>();
        try {
            readCustomers();
        } catch (FileNotFoundException ex) {
            System.err.println("Please ensure the required CSV files are present: " + ex.getMessage());
            System.exit(1);
        } catch (WarehouseException ex) {
            System.err.println("Failed to initialize the warehouse: " + ex.getMessage());
            System.exit(2);
        }
    }

    @Override
    public Collection<Customer> getCustomers() {
        return customers.values()
            .stream()
            .map(Customer::new)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Customer getCustomer(int id) {
        Customer customer = customers.get(id);
        return customer == null ? null : new Customer(customer);
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
}
