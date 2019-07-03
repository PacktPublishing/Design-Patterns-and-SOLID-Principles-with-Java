package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.WarehouseException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class RestCustomerDao implements CustomerDao {

    private static final String CUSTOMERS_URL = System.getenv()
        .getOrDefault("CUSTOMERS_URL", "http://localhost:4567/customers");

    private static final int CONNECT_TIMEOUT = Integer.valueOf(System.getenv()
        .getOrDefault("CONNECT_TIMEOUT", "10000"));

    private static final int READ_TIMEOUT = Integer.valueOf(System.getenv()
        .getOrDefault("READ_TIMEOUT", "5000"));

    private static Customer toCustomer(JSONObject c) {
        return new Customer(c.getInt("id"),
            c.getString("name"),
            LocalDate.parse(c.getString("date_of_birth")),
            c.getString("company"),
            c.getString("phone"),
            c.getString("street_address"),
            c.getString("city"),
            c.getString("state"),
            c.getInt("zip_code"));
    }

    @Override
    public Collection<Customer> getCustomers() throws WarehouseException {
        try {
            JSONArray customers = new JSONArray(get(CUSTOMERS_URL));
            return stream(customers.spliterator(), false)
                .map(JSONObject.class::cast)
                .map(RestCustomerDao::toCustomer)
                .sorted(Comparator.comparing(Customer::getId))
                .collect(toList());
        } catch (IOException ex) {
            throw new WarehouseException("Problem while fetching customers from API.", ex);
        }
    }

    @Override
    public Customer getCustomer(int id) throws WarehouseException {
        try {
            return toCustomer(new JSONObject(get(CUSTOMERS_URL + "/" + id)));
        } catch (IOException ex) {
            throw new WarehouseException(String.format("Problem while fetching customer (%s) from API", id), ex);
        }
    }

    private String get(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        try (InputStream is = connection.getInputStream()) {
            return new String(is.readAllBytes());
        }
    }
}
