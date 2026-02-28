package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for Stripe event handlers.
 * Provides common functionality for all handlers.
 */
public abstract class AbstractStripeEventHandler implements StripeEventHandler {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    public boolean canHandle(String eventType) {
        for (String supportedType : getSupportedEventTypes()) {
            if (supportedType.equals(eventType)) {
                return true;
            }
        }
        return false;
    }
}

