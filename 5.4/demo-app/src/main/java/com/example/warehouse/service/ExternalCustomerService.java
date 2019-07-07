package com.example.warehouse.service;

import com.example.warehouse.WarehouseException;
import org.json.JSONObject;

import java.util.Map;

public interface ExternalCustomerService {

    Map<Integer, JSONObject> fetchCustomers() throws WarehouseException;
}
