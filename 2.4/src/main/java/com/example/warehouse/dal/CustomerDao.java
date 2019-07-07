package com.example.warehouse.dal;

import com.example.warehouse.Customer;

import java.util.Collection;

public interface CustomerDao {

    Collection<Customer> getCustomers();

    Customer getCustomer(int id);
}
