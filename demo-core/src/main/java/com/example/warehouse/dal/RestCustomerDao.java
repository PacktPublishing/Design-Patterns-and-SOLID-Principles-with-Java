package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.WarehouseException;
import kong.unirest.UnirestException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

import static java.util.stream.Collectors.toList;

public class RestCustomerDao extends AbstractRestDao implements CustomerDao {

    private static final String CUSTOMERS_URL = System.getenv()
        .getOrDefault("CUSTOMERS_URL", "http://localhost:9090/customers");

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
            return getArray(CUSTOMERS_URL)
                .map(RestCustomerDao::toCustomer)
                .sorted(Comparator.comparing(Customer::getId))
                .collect(toList());
        } catch (UnirestException ex) {
            throw new WarehouseException("Problem while fetching customers from API.", ex);
        }
    }

    @Override
    public Customer getCustomer(int id) throws WarehouseException {
        try {
            return toCustomer(getObject(CUSTOMERS_URL + "/" + id));
        } catch (UnirestException ex) {
            throw new WarehouseException(String.format("Problem while fetching customer (%s) from API", id), ex);
        }
    }

    @Override
    public void deleteCustomer(int id) throws WarehouseException {
        try {
            deleteObject(CUSTOMERS_URL + "/" + id);
        } catch (UnirestException ex) {
            throw new WarehouseException(String.format("Problem while deleting customer (%s) via API", id), ex);
        }
    }
}
