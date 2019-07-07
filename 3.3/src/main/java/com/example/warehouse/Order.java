package com.example.warehouse;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public final class Order implements Comparable<Order> {

    private int id;
    private final Customer customer;
    private final LocalDate date;
    private final Map<Product, Integer> quantities;
    private boolean pending;

    public Order(Order order) {
        this(order.id, order.customer, order.date, order.quantities, order.pending);
    }

    public Order(int id, Customer customer, LocalDate date, Map<Product, Integer> quantities, boolean pending) {
        this(customer, date, quantities, pending);
        this.id = id;
    }

    public Order(Customer customer, Map<Product, Integer> quantities) {
        this(customer, LocalDate.now(), quantities, true);
    }

    public Order(Customer customer, LocalDate date, Map<Product, Integer> quantities) {
        this(customer, date, quantities, true);
    }

    public Order(Customer customer, LocalDate date, Map<Product, Integer> quantities, boolean pending) {
        this.customer = new Customer(customer);
        this.date = date;
        this.quantities = quantities.entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(e -> new Product(e.getKey()), Map.Entry::getValue));
        this.pending = pending;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
