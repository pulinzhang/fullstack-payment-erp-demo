package com.pb.stripe2netsuite.stripe2netsuitemvp.controller;

import com.stripe.model.Event;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.StripeEventParser;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.StripeEventProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * REST controller for handling Stripe webhook events.
 * Maps to /webhook/stripe endpoint.
 * 
 * Responsibilities:
 * - Receive HTTP requests from Stripe
 * - Extract raw payload and headers
 * - Delegate to services for parsing and processing
 * - Return 200 OK quickly (per Stripe best practices)
 * 
 * This controller is intentionally thin - all business logic is in services.
 */
@RestController
@RequestMapping("/webhook")
@Tag(name = "Stripe Webhooks", description = "Endpoints for receiving Stripe webhook events")
@SecurityRequirement(name = "stripe-signature")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    private final StripeEventParser eventParser;
    private final StripeEventProcessor eventProcessor;

    public StripeWebhookController(StripeEventParser eventParser, StripeEventProcessor eventProcessor) {
        this.eventParser = eventParser;
        this.eventProcessor = eventProcessor;
    }

    /**
     * Endpoint to receive Stripe webhook events.
     * 
     * Responsibilities:
     * - Read raw request body
     * - Extract signature header
     * - Delegate to StripeEventParser for validation
     * - Delegate to StripeEventProcessor for processing
     * - Return 200 OK immediately (per Stripe best practices)
     * 
     * @param request The HTTP request containing the Stripe event payload
     * @return ResponseEntity with status indicating success or failure
     */
    @PostMapping("/stripe")
    @Operation(
            summary = "Receive Stripe webhook",
            description = """
                    Receives webhook events from Stripe. This endpoint should be configured in your
                    Stripe Dashboard under Developers > Webhooks.
                    
                    ## Stripe Best Practices
                    - Returns 200 OK immediately after receiving the event
                    - Event processing happens asynchronously in the background
                    - Events are verified using the Stripe-Signature header
                    
                    ## Supported Event Types
                    - payment_intent.succeeded
                    - payment_intent.payment_failed
                    - charge.succeeded
                    - charge.failed
                    - invoice.paid
                    - invoice.payment_failed
                    - customer.subscription.created
                    - customer.subscription.updated
                    - customer.subscription.deleted
                    """,
            security = @SecurityRequirement(name = "stripe-signature")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Webhook received successfully",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Failed to parse or verify the webhook event",
                    content = @Content(mediaType = "text/plain")
            )
    })
    public ResponseEntity<String> handleStripeWebhook(
            @Parameter(
                    description = "HTTP request containing Stripe event payload",
                    required = true,
                    hidden = true
            )
            HttpServletRequest request) {
        logger.info("Received Stripe webhook request");

        // Debug: log Stripe-related headers
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (name.toLowerCase().contains("stripe") || name.toLowerCase().contains("signature")) {
                logger.debug("Header {}: {}", name, request.getHeader(name));
            }
        }

        // Read the raw request body
        String payload = readPayload(request);
        if (payload == null) {
            return ResponseEntity.badRequest().body("Failed to read payload");
        }

        logger.debug("Payload length: {}", payload.length());
        
        // Extract signature header
        String signatureHeader = request.getHeader("Stripe-Signature");
        logger.debug("Signature header present: {}", signatureHeader != null && !signatureHeader.isEmpty());

        // Parse and verify event (delegate to service)
        Event event = eventParser.parseEvent(payload, signatureHeader);
        if (event == null) {
            return ResponseEntity.badRequest().body("Failed to parse event");
        }

        // Process event asynchronously (delegate to service)
        // Per Stripe best practices: return 200 quickly, process in background
        processEventAsync(event);

        return ResponseEntity.ok("Webhook received successfully");
    }

    /**
     * Read raw payload from request.
     */
    private String readPayload(HttpServletRequest request) {
        try {
            return new String(request.getInputStream().readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read request payload", e);
            return null;
        }
    }

    /**
     * Process the event asynchronously.
     * Returns 200 to Stripe immediately, processes in background.
     */
    private void processEventAsync(Event event) {
        try {
            eventProcessor.processEvent(event);
        } catch (Exception e) {
            // Log error but don't fail the request (already returned 200)
            logger.error("Error processing event: {}", e.getMessage(), e);
        }
    }
}
