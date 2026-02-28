package com.pb.stripe2netsuite.stripe2netsuitemvp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.pb.stripe2netsuite.stripe2netsuitemvp.config.StripeWebhookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for parsing and verifying Stripe webhook events.
 * Handles signature verification and event deserialization.
 */
@Service
public class StripeEventParser {

    private static final Logger logger = LoggerFactory.getLogger(StripeEventParser.class);

    private final ObjectMapper objectMapper;
    private final StripeWebhookConfig webhookConfig;

    public StripeEventParser(ObjectMapper objectMapper, StripeWebhookConfig webhookConfig) {
        this.objectMapper = objectMapper;
        this.webhookConfig = webhookConfig;
    }

    /**
     * Parse and verify the Stripe event.
     * 
     * @param payload the raw request body
     * @param signatureHeader the Stripe-Signature header
     * @return the parsed Event, or null if parsing/verification fails
     */
    public Event parseEvent(String payload, String signatureHeader) {
        if (webhookConfig.isVerifySignature() && hasValidSecret()) {
            return parseEventWithVerification(payload, signatureHeader);
        } else {
            return parseEventWithoutVerification(payload);
        }
    }

    /**
     * Check if webhook secret is configured.
     */
    private boolean hasValidSecret() {
        return webhookConfig.getSecret() != null && !webhookConfig.getSecret().isEmpty();
    }

    /**
     * Parse event with signature verification.
     */
    private Event parseEventWithVerification(String payload, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isEmpty()) {
            logger.warn("Stripe-Signature header is missing");
            logger.warn("Available headers: check request headers");
            return null;
        }

        logger.debug("Signature header received: {}", signatureHeader.substring(0, Math.min(50, signatureHeader.length())) + "...");
        
        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, webhookConfig.getSecret());
            logger.info("Successfully verified webhook signature. Event type: {}", event.getType());
            return event;
        } catch (SignatureVerificationException e) {
            logger.error("Failed to verify Stripe webhook signature: {}", e.getMessage());
            logger.error("Webhook secret configured: {}", webhookConfig.getSecret() != null && !webhookConfig.getSecret().isEmpty());
            return null;
        }
    }

    /**
     * Parse event without signature verification (for testing only).
     */
    private Event parseEventWithoutVerification(String payload) {
        try {
            Event event = objectMapper.readValue(payload, Event.class);
            logger.warn("Webhook received WITHOUT signature verification. Event type: {}", event.getType());
            return event;
        } catch (Exception e) {
            logger.error("Failed to parse webhook payload as Event", e);
            return null;
        }
    }
}

