package com.example.warehouse;

import java.util.Objects;

public final class Product {

    private int id;
    private final String name;
    private final int price;

    public Product(Product product) {
        this(product.id, product.name, product.price);
    }

    public Product(int id, String name, int price) {
        this(name, price);
        this.id = id;
    }

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
