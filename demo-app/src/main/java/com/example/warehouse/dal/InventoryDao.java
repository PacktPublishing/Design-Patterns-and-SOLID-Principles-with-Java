package com.example.warehouse.dal;

import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;

import java.util.Map;

public interface InventoryDao {

    void updateStock(Map<Product, Integer> quantities) throws WarehouseException;
}
