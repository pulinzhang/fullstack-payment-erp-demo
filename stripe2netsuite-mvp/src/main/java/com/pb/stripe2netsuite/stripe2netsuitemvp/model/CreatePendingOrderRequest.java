package com.pb.stripe2netsuite.stripe2netsuitemvp.model;

/**
 * Request DTO for creating a pending order in mock mode.
 * This is used for demonstration purposes without Stripe integration.
 */
public class CreatePendingOrderRequest {
    
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

    public CreatePendingOrderRequest() {
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
}

