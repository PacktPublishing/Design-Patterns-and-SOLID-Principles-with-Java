package com.example.warehouse.dal;

import com.example.warehouse.Order;

import java.util.Collection;

public interface OrderDao {

    Collection<Order> getOrders();

    void addOrder(Order order);
}
