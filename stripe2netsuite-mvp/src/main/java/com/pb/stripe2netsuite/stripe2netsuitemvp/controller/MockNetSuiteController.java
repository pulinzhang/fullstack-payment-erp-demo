package com.pb.stripe2netsuite.stripe2netsuitemvp.controller;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.CreatePendingOrderRequest;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Customer;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.MockNetSuiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for Mock NetSuite operations.
 * Provides endpoints to view and manage mock orders and customers for demonstration purposes.
 * 
 * This controller is intended for development and testing only.
 * In production, these endpoints should be secured or removed.
 */
@RestController
@RequestMapping("/mock")
@Tag(name = "Mock NetSuite", description = "Mock NetSuite endpoints for demonstration and testing")
public class MockNetSuiteController {

    private static final Logger logger = LoggerFactory.getLogger(MockNetSuiteController.class);

    private final MockNetSuiteService mockNetSuiteService;

    public MockNetSuiteController(MockNetSuiteService mockNetSuiteService) {
        this.mockNetSuiteService = mockNetSuiteService;
    }

    /**
     * Get all mock orders.
     * 
     * @return list of all orders
     */
    @GetMapping("/orders")
    @Operation(
            summary = "Get all mock orders",
            description = "Retrieves all orders stored in the mock NetSuite database. " +
                    "Useful for testing and demonstration purposes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all orders",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))
            )
    })
    public ResponseEntity<List<Order>> getAllOrders() {
        logger.info("GET /mock/orders - Retrieving all mock orders");
        List<Order> orders = mockNetSuiteService.getAllOrders().values().stream()
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get a specific mock order by ID.
     * 
     * @param orderId the order ID
     * @return the order if found
     */
    @GetMapping("/orders/{id}")
    @Operation(
            summary = "Get mock order by ID",
            description = "Retrieves a specific order from the mock NetSuite database by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<Order> getOrder(
            @Parameter(
                    description = "The order ID (e.g., MOCK-ORDER-001)",
                    required = true,
                    example = "MOCK-ORDER-001"
            )
            @PathVariable("id") String orderId) {
        logger.info("GET /mock/orders/{} - Retrieving mock order", orderId);
        
        Order order = mockNetSuiteService.getOrder(orderId);
        
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            logger.warn("Order not found: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all mock customers.
     * 
     * @return list of all customers
     */
    @GetMapping("/customers")
    @Operation(
            summary = "Get all mock customers",
            description = "Retrieves all customers stored in the mock NetSuite database. " +
                    "Useful for testing and demonstration purposes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all customers",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))
            )
    })
    public ResponseEntity<List<Customer>> getAllCustomers() {
        logger.info("GET /mock/customers - Retrieving all mock customers");
        List<Customer> customers = mockNetSuiteService.getAllCustomers().values().stream()
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    /**
     * Get a specific mock customer by ID.
     * 
     * @param customerId the customer ID
     * @return the customer if found
     */
    @GetMapping("/customers/{id}")
    @Operation(
            summary = "Get mock customer by ID",
            description = "Retrieves a specific customer from the mock NetSuite database by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            )
    })
    public ResponseEntity<Customer> getCustomer(
            @Parameter(
                    description = "The customer ID (e.g., MOCK-CUST-001)",
                    required = true,
                    example = "MOCK-CUST-001"
            )
            @PathVariable("id") String customerId) {
        logger.info("GET /mock/customers/{} - Retrieving mock customer", customerId);
        
        Customer customer = mockNetSuiteService.getCustomer(customerId);
        
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            logger.warn("Customer not found: {}", customerId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update order status in mock NetSuite.
     * 
     * @param orderId the order ID
     * @param status the new status
     * @return the updated order
     */
    @PatchMapping("/orders/{id}/status")
    @Operation(
            summary = "Update mock order status",
            description = "Updates the status of an existing order in the mock NetSuite database."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<Order> updateOrderStatus(
            @Parameter(
                    description = "The order ID (e.g., MOCK-ORDER-001)",
                    required = true,
                    example = "MOCK-ORDER-001"
            )
            @PathVariable("id") String orderId,
            @Parameter(
                    description = "The new status",
                    required = true,
                    example = "completed"
            )
            @RequestParam String status) {
        logger.info("PATCH /mock/orders/{}/status - Updating order status to {}", orderId, status);
        
        Order order = mockNetSuiteService.updateOrderStatus(orderId, status);
        
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            logger.warn("Order not found for update: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Clear all mock data and reinitialize with sample data.
     * 
     * @return confirmation message
     */
    @DeleteMapping("/data")
    @Operation(
            summary = "Clear mock data",
            description = "Clears all mock data and reinitializes with sample data. " +
                    "Useful for testing and resetting the demonstration."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mock data cleared and reinitialized"
            )
    })
    public ResponseEntity<Map<String, String>> clearData() {
        logger.info("DELETE /mock/data - Clearing mock data");
        
        mockNetSuiteService.clearData();
        
        return ResponseEntity.ok(Map.of(
                "message", "Mock data cleared and reinitialized",
                "status", "success"
        ));
    }

    /**
     * Health check endpoint for mock service.
     * 
     * @return service status
     */
    @GetMapping("/health")
    @Operation(
            summary = "Mock service health check",
            description = "Returns the health status of the mock NetSuite service."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy"
            )
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
                "service", "MockNetSuite",
                "status", "UP",
                "ordersCount", mockNetSuiteService.getAllOrders().size(),
                "customersCount", mockNetSuiteService.getAllCustomers().size()
        );
        return ResponseEntity.ok(health);
    }

    /**
     * Create a new pending order for frontend demonstration.
     * This simulates a Stripe payment intent that has been created but not yet paid.
     * 
     * @param request the order details
     * @return the created pending order
     */
    @PostMapping("/orders/pending")
    @Operation(
            summary = "Create a pending order",
            description = "Creates a new pending order for frontend demonstration. " +
                    "This simulates a Stripe payment intent that has been created but not yet paid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pending order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    public ResponseEntity<Order> createPendingOrder(@RequestBody CreatePendingOrderRequest request) {
        logger.info("POST /mock/orders/pending - Creating pending order, amount: {}, currency: {}",
                request.getAmount(), request.getCurrency());
        
        if (request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        Order order = mockNetSuiteService.createPendingOrder(
                request.getAmount(),
                request.getCurrency(),
                request.getDescription(),
                null // No Stripe PaymentIntent ID in mock mode
        );
        
        return ResponseEntity.status(201).body(order);
    }
}

