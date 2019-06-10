package com.example.warehouse;

import java.time.LocalDate;

public final class Customer {

    private int id;
    private final String name;
    private LocalDate dateOfBirth;
    private String company;
    private String phone;
    private String streetAddress;
    private String city;
    private String state;
    private int zipCode;

    public Customer(Customer customer) {
        this(customer.id,
            customer.name,
            customer.dateOfBirth,
            customer.company,
            customer.phone,
            customer.streetAddress,
            customer.city,
            customer.state,
            customer.zipCode);
    }

    public Customer(int id, String name) {
        this(id, name, null, null, null, null, null, null, 0);
    }

    public Customer(
        int id,
        String name,
        LocalDate dateOfBirth,
        String company,
        String phone,
        String streetAddress,
        String city,
        String state,
        int zipCode) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.company = company;
        this.phone = phone;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCompany() {
        return company;
    }

    public String getPhone() {
        return phone;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getZipCode() {
        return zipCode;
    }
}
