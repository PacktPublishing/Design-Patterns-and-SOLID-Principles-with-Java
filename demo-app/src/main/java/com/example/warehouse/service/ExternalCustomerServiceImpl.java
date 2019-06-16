package com.example.warehouse.service;

import com.example.warehouse.WarehouseException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public final class ExternalCustomerServiceImpl implements ExternalCustomerService {

    private static final String EXTERNAL_CUSTOMERS_URL = System.getProperty("EXTERNAL_CUSTOMERS_URL", "http://localhost:3000/customers");

    @Override
    public Map<Integer, JSONObject> fetchCustomers() throws WarehouseException {
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

}
