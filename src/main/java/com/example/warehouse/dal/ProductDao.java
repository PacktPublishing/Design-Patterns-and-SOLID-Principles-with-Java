package com.example.warehouse.dal;

import com.example.warehouse.Product;

import java.util.Collection;

public interface ProductDao {

    Collection<Product> getProducts();

    Product getProduct(int id);

    void addProduct(Product product);
}
