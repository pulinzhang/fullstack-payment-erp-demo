package com.pb.stripe2netsuite.stripe2netsuitemvp.service;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.pb.stripe2netsuite.stripe2netsuitemvp.handler.StripeEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for processing Stripe webhook events.
 * Routes events to the appropriate handler based on event type.
 */
@Service
public class StripeEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StripeEventProcessor.class);

    private final List<StripeEventHandler> handlers;

    public StripeEventProcessor(List<StripeEventHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Process a Stripe event by routing it to the appropriate handler.
     * 
     * @param event the Stripe event to process
     */
    public void processEvent(Event event) {
        String eventType = event.getType();
        String eventId = event.getId();
        
        logger.info("Processing event type: {}, eventId: {}", eventType, eventId);

        // Deserialize the Stripe object from the event
        StripeObject stripeObject = deserializeEventData(event);
        
        if (stripeObject == null) {
            logger.error("Failed to deserialize event data for event type: {}", eventType);
            return;
        }

        // Find and execute the appropriate handler
        StripeEventHandler handler = findHandler(eventType);
        
        if (handler != null) {
            logger.debug("Routing event {} to handler {}", eventType, handler.getClass().getSimpleName());
            handler.handle(eventType, eventId, stripeObject);
        } else {
            logger.warn("No handler found for event type: {}", eventType);
        }
    }

    /**
     * Deserialize the event data object.
     * 
     * @param event the Stripe event
     * @return the deserialized Stripe object, or null if deserialization fails
     */
    private StripeObject deserializeEventData(Event event) {

        EventDataObjectDeserializer deserializer =
                event.getDataObjectDeserializer();

        // First try safe deserialization
        if (deserializer.getObject().isPresent()) {
            return deserializer.getObject().get();
        }

        logger.warn("Safe deserialization failed for event type: {}. Trying unsafe deserialization.",
                event.getType());

        // Fallback to unsafe deserialization (API version mismatch safe fallback)
        try {
            return deserializer.deserializeUnsafe();
        } catch (Exception e) {
            logger.error("Unsafe deserialization also failed for event type: {}",
                    event.getType(), e);
            return null;
        }
    }
    /**
     * Find the appropriate handler for the given event type.
     * 
     * @param eventType the Stripe event type
     * @return the handler, or null if no handler is found
     */
    private StripeEventHandler findHandler(String eventType) {
        logger.debug("Looking for handler for event type: {}", eventType);
        logger.debug("Available handlers: {}", handlers.stream()
                .map(h -> h.getClass().getSimpleName())
                .toList());
        
        for (StripeEventHandler handler : handlers) {
            logger.debug("Checking handler {} for event type {}: canHandle={}", 
                handler.getClass().getSimpleName(), eventType, handler.canHandle(eventType));
            if (handler.canHandle(eventType)) {
                return handler;
            }
        }
        return null;
    }
}

