package com.pb.stripe2netsuite.stripe2netsuitemvp.model;

/**
 * Response DTO for order creation.
 * Contains all data needed by frontend to complete payment with Stripe.
 */
public class CreateOrderResponse {
    
    /**
     * The mock order ID in NetSuite.
     */
    private String orderId;
    
    /**
     * Order status (e.g., "pending", "paid", "failed").
     */
    private String status;
    
    /**
     * Order amount in smallest currency unit.
     */
    private Long amount;
    
    /**
     * Currency code (ISO 4217).
     */
    private String currency;
    
    /**
     * Stripe PaymentIntent client secret.
     * Frontend uses this with Stripe.js to confirm payment.
     * Format: {payment_intent_id}_{client_secret}
     */
    private String clientSecret;
    
    /**
     * Stripe PaymentIntent ID.
     */
    private String paymentIntentId;

    public CreateOrderResponse() {
    }

    public CreateOrderResponse(String orderId, String status, Long amount, 
                               String currency, String clientSecret, String paymentIntentId) {
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
}

