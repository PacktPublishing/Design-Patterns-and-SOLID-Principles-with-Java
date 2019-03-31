package com.example.warehouse.dal;

import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;

import java.util.Collection;

public interface ProductDao {

    Collection<Product> getProducts() throws WarehouseException;

    Product getProduct(int id) throws WarehouseException;

    void addProduct(Product product) throws WarehouseException;
}
