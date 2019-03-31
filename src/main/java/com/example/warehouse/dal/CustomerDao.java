package com.example.warehouse.dal;

import com.example.warehouse.Customer;
import com.example.warehouse.WarehouseException;

import java.util.Collection;

public interface CustomerDao {

    Collection<Customer> getCustomers() throws WarehouseException;

    Customer getCustomer(int id) throws WarehouseException;
}
