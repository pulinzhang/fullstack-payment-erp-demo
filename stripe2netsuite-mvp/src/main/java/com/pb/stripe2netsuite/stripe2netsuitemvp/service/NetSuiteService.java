package com.pb.stripe2netsuite.stripe2netsuitemvp.service;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Customer;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.OrderDto;

/**
 * Service interface for NetSuite integration.
 * Defines the contract for creating orders, updating order status, and retrieving customers.
 * 
 * This interface allows for different implementations:
 * - MockNetSuiteService: For development and testing
 * - RealNetSuiteService: For production (requires NetSuite credentials)
 */
public interface NetSuiteService {
    
    /**
     * Create a new order in NetSuite.
     * 
     * @param dto the order data transfer object containing order details
     * @return the created order with ID and status
     */
    Order createOrder(OrderDto dto);
    
    /**
     * Update the status of an existing order in NetSuite.
     * 
     * @param orderId the NetSuite order ID
     * @param status the new status to set
     * @return the updated order
     */
    Order updateOrderStatus(String orderId, String status);
    
    /**
     * Get a customer from NetSuite by ID.
     * 
     * @param customerId the NetSuite customer ID
     * @return the customer if found, null otherwise
     */
    Customer getCustomer(String customerId);
    
    /**
     * Mark an order as paid using the Stripe PaymentIntent ID.
     * This is called from the webhook handler when payment_intent.succeeded is received.
     * 
     * @param stripePaymentIntentId the Stripe PaymentIntent ID
     * @return the updated order, or null if not found
     */
    Order markOrderAsPaid(String stripePaymentIntentId);
}

