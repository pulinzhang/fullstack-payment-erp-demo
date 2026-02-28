package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import com.stripe.model.StripeObject;

/**
 * Interface for handling Stripe webhook events.
 * Each handler is responsible for a specific category of events.
 */
public interface StripeEventHandler {
    
    /**
     * Check if this handler can handle the given event type.
     * 
     * @param eventType the Stripe event type (e.g., "payment_intent.succeeded")
     * @return true if this handler can handle the event
     */
    boolean canHandle(String eventType);
    
    /**
     * Handle the Stripe event.
     * 
     * @param eventType the Stripe event type (e.g., "payment_intent.succeeded")
     * @param eventId the Stripe event ID
     * @param stripeObject the deserialized Stripe object from the event
     */
    void handle(String eventType, String eventId, StripeObject stripeObject);
    
    /**
     * Get the event types this handler supports.
     * 
     * @return array of event type strings
     */
    String[] getSupportedEventTypes();
}

