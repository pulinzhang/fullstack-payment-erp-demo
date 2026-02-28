package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import com.stripe.model.Customer;
import com.stripe.model.StripeObject;
import org.springframework.stereotype.Component;

/**
 * Handler for Customer-related webhook events.
 */
@Component
public class CustomerEventHandler extends AbstractStripeEventHandler {
    
    @Override
    public String[] getSupportedEventTypes() {
        return new String[] {
            "customer.created",
            "customer.updated",
            "customer.deleted"
        };
    }
    
    @Override
    public void handle(String eventType, String eventId, StripeObject stripeObject) {
        Customer customer = (Customer) stripeObject;
        
        // Use eventType from the parameter, not from getObject()
        switch (eventType) {
            case "customer.created":
                handleCustomerCreated(eventId, customer);
                break;
            case "customer.updated":
                handleCustomerUpdated(eventId, customer);
                break;
            case "customer.deleted":
                handleCustomerDeleted(eventId, customer);
                break;
            default:
                logger.info("Unhandled Customer event: {}", eventId);
        }
    }
    
    private void handleCustomerCreated(String eventId, Customer customer) {
        logger.info("Customer created - eventId: {}, email: {}, name: {}", 
            eventId, customer.getEmail(), customer.getName());
        
        // TODO: Sync customer to NetSuite
        // - Create Customer record
    }
    
    private void handleCustomerUpdated(String eventId, Customer customer) {
        logger.info("Customer updated - eventId: {}", eventId);
        
        // TODO: Update customer in NetSuite
    }
    
    private void handleCustomerDeleted(String eventId, Customer customer) {
        logger.info("Customer deleted - eventId: {}", eventId);
        
        // TODO: Handle customer deletion in NetSuite
        // - Mark as inactive or delete
    }
}

