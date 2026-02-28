package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import org.springframework.stereotype.Component;

/**
 * Handler for Subscription-related webhook events.
 */
@Component
public class SubscriptionEventHandler extends AbstractStripeEventHandler {
    
    @Override
    public String[] getSupportedEventTypes() {
        return new String[] {
            "subscription.created",
            "subscription.updated",
            "subscription.deleted",
            "subscription.paused",
            "subscription.resumed",
            "subscription.trial_will_end"
        };
    }
    
    @Override
    public void handle(String eventType, String eventId, StripeObject stripeObject) {
        Subscription subscription = (Subscription) stripeObject;
        
        // Use eventType from the parameter, not from getObject()
        switch (eventType) {
            case "subscription.created":
                handleSubscriptionCreated(eventId, subscription);
                break;
            case "subscription.updated":
                handleSubscriptionUpdated(eventId, subscription);
                break;
            case "subscription.deleted":
                handleSubscriptionDeleted(eventId, subscription);
                break;
            case "subscription.paused":
                handleSubscriptionPaused(eventId, subscription);
                break;
            case "subscription.resumed":
                handleSubscriptionResumed(eventId, subscription);
                break;
            case "subscription.trial_will_end":
                handleSubscriptionTrialWillEnd(eventId, subscription);
                break;
            default:
                logger.info("Unhandled Subscription event: {}", eventId);
        }
    }
    
    private void handleSubscriptionCreated(String eventId, Subscription subscription) {
        logger.info("Subscription created - eventId: {}, customer: {}, status: {}", 
            eventId, subscription.getCustomer(), subscription.getStatus());
        
        // TODO: Sync subscription to NetSuite
        // - Create Subscription/Contract record
    }
    
    private void handleSubscriptionUpdated(String eventId, Subscription subscription) {
        logger.info("Subscription updated - eventId: {}, status: {}", 
            eventId, subscription.getStatus());
        
        // TODO: Update subscription in NetSuite
    }
    
    private void handleSubscriptionDeleted(String eventId, Subscription subscription) {
        logger.info("Subscription deleted - eventId: {}", eventId);
        
        // TODO: Handle subscription cancellation in NetSuite
    }
    
    private void handleSubscriptionPaused(String eventId, Subscription subscription) {
        logger.info("Subscription paused - eventId: {}", eventId);
        
        // TODO: Handle paused subscription in NetSuite
    }
    
    private void handleSubscriptionResumed(String eventId, Subscription subscription) {
        logger.info("Subscription resumed - eventId: {}", eventId);
        
        // TODO: Handle resumed subscription in NetSuite
    }
    
    private void handleSubscriptionTrialWillEnd(String eventId, Subscription subscription) {
        logger.info("Subscription trial will end - eventId: {}", eventId);
        
        // TODO: Send reminder about trial ending
    }
}

