package com.hotel.model;

import java.util.UUID;

public class Customer {
    private final String customerId;
    private final String name;
    private final String email;
    private final String phone;

    public Customer(String name, String email, String phone) {
        this.customerId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    public Customer(String customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    public String getCustomerId() { return customerId; }
    public String getName()       { return name; }
    public String getEmail()      { return email; }
    public String getPhone()      { return phone; }

    @Override
    public String toString() {
        return String.format("Customer[%s] %s | %s | %s", customerId, name, email, phone);
    }
}
