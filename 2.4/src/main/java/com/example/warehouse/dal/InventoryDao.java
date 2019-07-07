package com.example.warehouse.dal;

import com.example.warehouse.Product;

import java.util.Map;

public interface InventoryDao {

    void updateStock(Map<Product, Integer> quantities);
}
