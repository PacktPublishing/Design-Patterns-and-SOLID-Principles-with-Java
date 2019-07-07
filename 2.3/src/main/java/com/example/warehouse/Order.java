package com.example.warehouse;

import java.time.LocalDate;
import java.util.Map;

public final class Order implements Comparable<Order> {

    private int id;
    private Customer customer;
    private LocalDate date;
    private Map<Product, Integer> quantities;
    private boolean pending;

    Order(int id, Customer customer, LocalDate date, Map<Product, Integer> quantities, boolean pending) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.quantities = quantities;
        this.pending = pending;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<Product, Integer> getQuantities() {
        return quantities;
    }

    public boolean isPending() {
        return pending;
    }

    public int getTotalPrice() {
        return quantities.entrySet()
            .stream()
            .mapToInt(e -> e.getKey().getPrice() * e.getValue())
            .sum();
    }

    @Override
    public int compareTo(Order o) {
        return date.compareTo(o.date);
    }
}
