package com.pb.stripe2netsuite.stripe2netsuitemvp.service;

import com.pb.stripe2netsuite.stripe2netsuitemvp.config.StripeConfig;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.CreateOrderRequest;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.CreateOrderResponse;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for handling order creation with Stripe PaymentIntent.
 * This service coordinates between the frontend request, MockNetSuite, and Stripe API.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final MockNetSuiteService mockNetSuiteService;
    private final StripeConfig stripeConfig;

    public OrderService(MockNetSuiteService mockNetSuiteService, StripeConfig stripeConfig) {
        this.mockNetSuiteService = mockNetSuiteService;
        this.stripeConfig = stripeConfig;
    }

    /**
     * Create a new order with a Stripe PaymentIntent.
     * 
     * Steps:
     * 1. Create a Stripe PaymentIntent with metadata containing a placeholder order ID
     * 2. Create a pending order in MockNetSuite with the real Stripe PaymentIntent ID
     * 3. Return the response with clientSecret for frontend payment
     * 
     * @param request the order creation request
     * @return the order response with Stripe clientSecret
     * @throws StripeException if Stripe API call fails
     */
    public CreateOrderResponse createOrder(CreateOrderRequest request) throws StripeException {
        logger.info("Creating order - amount: {}, currency: {}, description: {}", 
                request.getAmount(), request.getCurrency(), request.getDescription());

        // Step 1: Generate order ID first (for metadata)
        // We'll create the actual order after getting the PaymentIntent ID
        String tempOrderId = "pending-" + System.currentTimeMillis();

        // Step 2: Create Stripe PaymentIntent with metadata containing the order ID we'll use
        PaymentIntent paymentIntent = createStripePaymentIntent(request, tempOrderId);
        
        String paymentIntentId = paymentIntent.getId();
        String clientSecret = paymentIntent.getClientSecret();

        logger.info("Created PaymentIntent: {}, clientSecret: {}", 
                paymentIntentId, clientSecret);

        // Step 3: Create pending order in MockNetSuite with real Stripe PaymentIntent ID
        Order pendingOrder = mockNetSuiteService.createPendingOrder(
                request.getAmount(),
                request.getCurrency(),
                request.getDescription(),
                paymentIntentId // Use the real Stripe PaymentIntent ID
        );

        String orderId = pendingOrder.getId();

        // Step 4: Return the response
        return new CreateOrderResponse(
                orderId,
                "pending",
                request.getAmount(),
                request.getCurrency() != null ? request.getCurrency() : "usd",
                clientSecret,
                paymentIntentId
        );
    }

    /**
     * Create a Stripe PaymentIntent with metadata.
     * 
     * @param request the order request
     * @param orderId the mock order ID to include in metadata
     * @return the created PaymentIntent
     * @throws StripeException if Stripe API call fails
     */
    private PaymentIntent createStripePaymentIntent(CreateOrderRequest request, String orderId) 
            throws StripeException {
        
        // Build PaymentIntent create params
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency() != null ? request.getCurrency() : "usd")
                .putMetadata("orderId", orderId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                );

        // Add description to metadata if provided
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            paramsBuilder.putMetadata("description", request.getDescription());
            paramsBuilder.setDescription(request.getDescription());
        }

        // Create and return the PaymentIntent
        return PaymentIntent.create(paramsBuilder.build());
    }

    /**
     * Get the Stripe publishable key for frontend.
     * 
     * @return the publishable key
     */
    public String getPublishableKey() {
        return stripeConfig.getPublishableKey();
    }
}

