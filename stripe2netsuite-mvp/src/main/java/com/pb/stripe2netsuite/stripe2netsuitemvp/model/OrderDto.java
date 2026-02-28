package com.pb.stripe2netsuite.stripe2netsuitemvp.model;

/**
 * Data Transfer Object for creating an Order in NetSuite.
 * This represents the data received from Stripe events.
 */
public class OrderDto {
    
    private String stripePaymentId;
    private String stripeCustomerId;
    private Long amount;
    private String currency;
    private String description;
    private String status;
    
    public OrderDto() {
    }
    
    // Getters and Setters
    public String getStripePaymentId() {
        return stripePaymentId;
    }
    
    public void setStripePaymentId(String stripePaymentId) {
        this.stripePaymentId = stripePaymentId;
    }
    
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }
    
    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "OrderDto{" +
                "stripePaymentId='" + stripePaymentId + '\'' +
                ", stripeCustomerId='" + stripeCustomerId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

