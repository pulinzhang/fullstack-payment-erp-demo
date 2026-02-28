package com.pb.stripe2netsuite.stripe2netsuitemvp.controller;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.CreateOrderRequest;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.CreateOrderResponse;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.OrderService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for order operations.
 * Provides endpoints for creating orders with Stripe PaymentIntent.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order creation and management endpoints")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order with Stripe PaymentIntent.
     * 
     * This endpoint handles:
     * 1. Creating a pending order in MockNetSuite
     * 2. Creating a Stripe PaymentIntent with metadata containing the order ID
     * 3. Returning the clientSecret to the frontend for payment confirmation
     * 
     * The frontend should ONLY call stripe.confirmCardPayment(clientSecret) 
     * to complete the payment. All Stripe logic remains in the backend.
     * 
     * @param request the order creation request
     * @return the order response with Stripe clientSecret
     */
    @PostMapping("/create")
    @Operation(
            summary = "Create order with Stripe PaymentIntent",
            description = """
                    Creates a new order and Stripe PaymentIntent.
                    
                    ## Flow:
                    1. Backend creates a pending order in MockNetSuite
                    2. Backend creates a Stripe PaymentIntent with metadata.orderId
                    3. Backend returns clientSecret to frontend
                    4. Frontend uses clientSecret to confirm payment with Stripe.js
                    
                    ## Frontend Usage:
                    ```javascript
                    // After receiving response from this endpoint:
                    const {error, paymentIntent} = await stripe.confirmCardPayment(clientSecret, {
                        payment_method: {card: cardElement}
                    });
                    ```
                    
                    ## Important:
                    - Do NOT create PaymentIntent on frontend
                    - Do NOT calculate amount on frontend
                    - All Stripe logic stays in backend
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully with PaymentIntent",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - amount must be positive"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Stripe API error"
            )
    })
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        logger.info("POST /api/orders/create - Creating order with Stripe PaymentIntent");

        // Validate request
        if (request.getAmount() == null || request.getAmount() <= 0) {
            logger.warn("Invalid order request: amount must be positive");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }

        try {
            CreateOrderResponse response = orderService.createOrder(request);
            logger.info("Order created successfully - orderId: {}, paymentIntentId: {}", 
                    response.getOrderId(), response.getPaymentIntentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (StripeException e) {
            logger.error("Stripe API error during order creation: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to create Stripe PaymentIntent: " + e.getMessage());
        }
    }

    /**
     * Get the Stripe publishable key for frontend.
     * 
     * Frontend uses this to initialize Stripe.js:
     * ```javascript
     * const stripe = Stripe(publishableKey);
     * ```
     * 
     * @return the publishable key
     */
    @GetMapping("/config")
    @Operation(
            summary = "Get Stripe configuration",
            description = "Returns the Stripe publishable key for frontend initialization."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stripe configuration retrieved"
            )
    })
    public ResponseEntity<java.util.Map<String, String>> getStripeConfig() {
        String publishableKey = orderService.getPublishableKey();
        return ResponseEntity.ok(java.util.Map.of("publishableKey", publishableKey));
    }
}

