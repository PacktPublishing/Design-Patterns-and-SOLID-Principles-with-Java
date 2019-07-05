package com.example.warehouse.dal;

import com.example.warehouse.Order;
import com.example.warehouse.WarehouseException;

import java.util.Collection;

public interface OrderDao {

    Collection<Order> getOrders() throws WarehouseException;

    Order getOrder(int id) throws WarehouseException;

    void addOrder(Order order) throws WarehouseException;
}
