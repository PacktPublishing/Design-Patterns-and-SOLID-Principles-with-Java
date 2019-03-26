package com.example.warehouse;

public final class Customer {

    private int id;
    private final String name;

    public Customer(Customer customer) {
        this(customer.id, customer.name);
    }

    public Customer(int id, String name) {
        this(name);
        this.id = id;
    }

    public Customer(String name) {
        this.name = name;
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
}
