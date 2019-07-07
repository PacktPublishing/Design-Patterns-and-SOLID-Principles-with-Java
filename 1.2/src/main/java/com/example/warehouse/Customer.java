package com.example.warehouse;

public final class Customer {

    private final int id;
    private final String name;

    Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
