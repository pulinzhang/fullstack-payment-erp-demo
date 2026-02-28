package com.pb.stripe2netsuite.stripe2netsuitemvp.model;

/**
 * Domain model representing an Order in NetSuite.
 * This is a simplified mock representation for demonstration purposes.
 */
public class Order {
    
    private String id;
    private String status;
    private Long amount;
    private String currency;
    private String customerId;
    private String stripePaymentId;
    private String description;
    
    public Order() {
    }
    
    public Order(String id, String status, Long amount, String currency) {
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getStripePaymentId() {
        return stripePaymentId;
    }
    
    public void setStripePaymentId(String stripePaymentId) {
        this.stripePaymentId = stripePaymentId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", customerId='" + customerId + '\'' +
                ", stripePaymentId='" + stripePaymentId + '\'' +
                '}';
    }
}

