package com.pb.stripe2netsuite.stripe2netsuitemvp.model;

/**
 * Domain model representing a Customer in NetSuite.
 * This is a simplified mock representation for demonstration purposes.
 */
public class Customer {
    
    private String id;
    private String name;
    private String email;
    private String stripeCustomerId;
    
    public Customer() {
    }
    
    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Customer(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }
    
    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", stripeCustomerId='" + stripeCustomerId + '\'' +
                '}';
    }
}

