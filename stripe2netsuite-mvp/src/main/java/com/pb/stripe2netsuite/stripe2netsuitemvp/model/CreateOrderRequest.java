package com.pb.stripe2netsuite.stripe2netsuitemvp.model;

/**
 * Request DTO for creating a new order.
 * Frontend sends this to create a pending order with Stripe PaymentIntent.
 */
public class CreateOrderRequest {
    
    /**
     * Order amount in smallest currency unit (e.g., cents for USD).
     * Example: 9999 = $99.99 USD
     */
    private Long amount;
    
    /**
     * Currency code (ISO 4217).
     * Example: "usd", "eur", "gbp"
     */
    private String currency;
    
    /**
     * Optional description for the order.
     */
    private String description;
    
    /**
     * Optional customer email (for Stripe customer).
     */
    private String customerEmail;

    public CreateOrderRequest() {
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}

