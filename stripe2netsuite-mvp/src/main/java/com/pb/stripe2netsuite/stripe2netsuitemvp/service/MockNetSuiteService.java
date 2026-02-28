package com.pb.stripe2netsuite.stripe2netsuitemvp.service;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Customer;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of NetSuiteService for development and testing.
 * 
 * This service simulates NetSuite operations without connecting to the real NetSuite API.
 * All operations are logged to the console for demonstration purposes.
 * 
 * Features:
 * - In-memory storage for orders and customers
 * - Console logging of all operations
 * - Simulated response delays for realistic behavior
 * - Thread-safe operations using ConcurrentHashMap
 */
@Service
public class MockNetSuiteService implements NetSuiteService {

    private static final Logger logger = LoggerFactory.getLogger(MockNetSuiteService.class);

    /**
     * In-memory storage for mock orders.
     * Key: order ID, Value: Order object
     */
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    /**
     * In-memory index for finding orders by Stripe PaymentIntent ID.
     * Key: Stripe PaymentIntent ID, Value: Order ID
     */
    private final Map<String, String> ordersByStripePaymentId = new ConcurrentHashMap<>();

    /**
     * In-memory storage for mock customers.
     * Key: customer ID, Value: Customer object
     */
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();

    /**
     * Counter for generating mock order IDs.
     */
    private int orderCounter = 0;

    /**
     * Counter for generating mock customer IDs.
     */
    private int customerCounter = 0;

    public MockNetSuiteService() {
        // Initialize with some sample data
        initializeSampleData();
    }

    /**
     * Initialize sample data for demonstration.
     */
    private void initializeSampleData() {
        // Create sample customers
        Customer customer1 = new Customer("MOCK-CUST-001", "John Doe", "john.doe@example.com");
        customer1.setStripeCustomerId("cus_1234567890");
        customers.put(customer1.getId(), customer1);

        Customer customer2 = new Customer("MOCK-CUST-002", "Jane Smith", "jane.smith@example.com");
        customer2.setStripeCustomerId("cus_0987654321");
        customers.put(customer2.getId(), customer2);

        // Create sample orders
        Order order1 = new Order("MOCK-ORDER-001", "pending", 5000L, "usd");
        order1.setCustomerId("MOCK-CUST-001");
        order1.setStripePaymentId("pi_1234567890");
        order1.setDescription("Sample order from Stripe payment");
        orders.put(order1.getId(), order1);
        ordersByStripePaymentId.put(order1.getStripePaymentId(), order1.getId());

        orderCounter = 1;
        customerCounter = 2;

        logger.info("[MockNetSuite] Sample data initialized: {} customers, {} orders",
                customers.size(), orders.size());
    }

    /**
     * Create a new order in the mock NetSuite.
     * 
     * @param dto the order data
     * @return the created order with generated ID
     */
    @Override
    public Order createOrder(OrderDto dto) {
        // Generate unique mock order ID
        orderCounter++;
        String orderId = String.format("MOCK-ORDER-%03d", orderCounter);
        
        // Get or create customer
        String customerId = dto.getStripeCustomerId();
        if (customerId != null && !customerId.isEmpty()) {
            // Try to find existing customer by Stripe ID
            customerId = findCustomerByStripeId(customerId);
            if (customerId == null) {
                // Create a new mock customer
                customerId = createMockCustomer(dto.getStripeCustomerId());
            }
        }

        // Create the order
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : "pending");
        order.setAmount(dto.getAmount());
        order.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "usd");
        order.setCustomerId(customerId);
        order.setStripePaymentId(dto.getStripePaymentId());
        order.setDescription(dto.getDescription());

        // Store in memory
        orders.put(orderId, order);

        // Log the action
        logger.info("[MockNetSuite] action: create, eventId: {}, orderId: {}, amount: {}, currency: {}, status: {}",
                dto.getStripePaymentId(),
                orderId,
                order.getAmount(),
                order.getCurrency(),
                order.getStatus());

        return order;
    }

    /**
     * Update the status of an existing order.
     * 
     * @param orderId the order ID
     * @param status the new status
     * @return the updated order
     */
    @Override
    public Order updateOrderStatus(String orderId, String status) {
        Order order = orders.get(orderId);
        
        if (order == null) {
            logger.warn("[MockNetSuite] action: update, orderId: {} - NOT FOUND", orderId);
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(status);
        orders.put(orderId, order);

        // Log the action
        logger.info("[MockNetSuite] action: update, orderId: {}, oldStatus: {}, newStatus: {}",
                orderId, oldStatus, status);

        return order;
    }

    /**
     * Get a customer by ID.
     * 
     * @param customerId the customer ID
     * @return the customer if found, null otherwise
     */
    @Override
    public Customer getCustomer(String customerId) {
        // Log the action
        logger.info("[MockNetSuite] action: get, customerId: {}", customerId);

        Customer customer = customers.get(customerId);
        
        if (customer == null) {
            logger.warn("[MockNetSuite] Customer not found: {}", customerId);
        }
        
        return customer;
    }

    /**
     * Find a customer by Stripe customer ID.
     * 
     * @param stripeCustomerId the Stripe customer ID
     * @return the NetSuite customer ID, or null if not found
     */
    private String findCustomerByStripeId(String stripeCustomerId) {
        for (Customer customer : customers.values()) {
            if (stripeCustomerId.equals(customer.getStripeCustomerId())) {
                return customer.getId();
            }
        }
        return null;
    }

    /**
     * Create a new mock customer.
     * 
     * @param stripeCustomerId the Stripe customer ID
     * @return the NetSuite customer ID
     */
    private String createMockCustomer(String stripeCustomerId) {
        customerCounter++;
        String customerId = String.format("MOCK-CUST-%03d", customerCounter);
        
        Customer customer = new Customer(customerId, "Customer " + customerId, "customer" + customerCounter + "@example.com");
        customer.setStripeCustomerId(stripeCustomerId);
        
        customers.put(customerId, customer);
        
        logger.info("[MockNetSuite] Created new mock customer: {}, stripeCustomerId: {}",
                customerId, stripeCustomerId);
        
        return customerId;
    }

    /**
     * Get all mock orders (for testing/debugging).
     * 
     * @return map of all orders
     */
    public Map<String, Order> getAllOrders() {
        return new ConcurrentHashMap<>(orders);
    }

    /**
     * Get all mock customers (for testing/debugging).
     * 
     * @return map of all customers
     */
    public Map<String, Customer> getAllCustomers() {
        return new ConcurrentHashMap<>(customers);
    }

    /**
     * Get an order by ID (for testing/debugging).
     * 
     * @param orderId the order ID
     * @return the order if found, null otherwise
     */
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    /**
     * Clear all mock data (for testing).
     */
    public void clearData() {
        orders.clear();
        ordersByStripePaymentId.clear();
        customers.clear();
        orderCounter = 0;
        customerCounter = 0;
        initializeSampleData();
        logger.info("[MockNetSuite] All mock data cleared and reinitialized");
    }

    /**
     * Create a new pending order for frontend demonstration.
     * This simulates a Stripe payment intent that has been created but not yet paid.
     * 
     * @param amount order amount in smallest currency unit
     * @param currency currency code (e.g., "usd")
     * @param description order description
     * @param stripePaymentIntentId the Stripe PaymentIntent ID (from Stripe API)
     * @return the created pending order
     */
    public Order createPendingOrder(Long amount, String currency, String description, String stripePaymentIntentId) {
        // Generate unique mock order ID
        orderCounter++;
        String orderId = String.format("MOCK-ORDER-%03d", orderCounter);
        
        // Use provided Stripe PaymentIntent ID or generate mock one
        String stripePaymentId = stripePaymentIntentId != null ? stripePaymentIntentId : 
            "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        
        // Use existing customer or create new one
        String customerId = "MOCK-CUST-001";
        
        // Create the pending order
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("pending");
        order.setAmount(amount);
        order.setCurrency(currency != null ? currency : "usd");
        order.setCustomerId(customerId);
        order.setStripePaymentId(stripePaymentId);
        order.setDescription(description);
        
        // Store in memory
        orders.put(orderId, order);
        
        // Also store by Stripe PaymentIntent ID for quick lookup
        ordersByStripePaymentId.put(stripePaymentId, orderId);
        
        // Log the action
        logger.info("[MockNetSuite] action: createPending, orderId: {}, amount: {}, currency: {}, status: pending, stripePaymentId: {}",
                orderId,
                order.getAmount(),
                order.getCurrency(),
                stripePaymentId);
        
        return order;
    }
    
    /**
     * Find an order by Stripe PaymentIntent ID.
     * 
     * @param stripePaymentIntentId the Stripe PaymentIntent ID
     * @return the order if found, null otherwise
     */
    public Order findOrderByStripePaymentIntentId(String stripePaymentIntentId) {
        String orderId = ordersByStripePaymentId.get(stripePaymentIntentId);
        if (orderId != null) {
            return orders.get(orderId);
        }
        return null;
    }
    
    /**
     * Update order status to paid after successful payment.
     * This is called from the webhook handler when payment_intent.succeeded is received.
     * 
     * @param stripePaymentIntentId the Stripe PaymentIntent ID
     * @return the updated order, or null if not found
     */
    @Override
    public Order markOrderAsPaid(String stripePaymentIntentId) {
        Order order = findOrderByStripePaymentIntentId(stripePaymentIntentId);
        if (order != null) {
            String oldStatus = order.getStatus();
            order.setStatus("paid");
            orders.put(order.getId(), order);
            logger.info("[MockNetSuite] action: markPaid, orderId: {}, oldStatus: {}, newStatus: {}",
                    order.getId(), oldStatus, "paid");
            return order;
        }
        logger.warn("[MockNetSuite] action: markPaid - Order not found for PaymentIntent: {}", stripePaymentIntentId);
        return null;
    }
}

