package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.OrderDto;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.NetSuiteService;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import org.springframework.stereotype.Component;

/**
 * Handler for PaymentIntent-related webhook events.
 */
@Component
public class PaymentIntentEventHandler extends AbstractStripeEventHandler {
    
    private final NetSuiteService netSuiteService;
    
    public PaymentIntentEventHandler(NetSuiteService netSuiteService) {
        this.netSuiteService = netSuiteService;
    }
    @Override
    public String[] getSupportedEventTypes() {
        return new String[] {
            "payment_intent.succeeded",
            "payment_intent.payment_failed",
            "payment_intent.created",
            "payment_intent.canceled",
            "payment_intent.requires_action"
        };
    }
    
    @Override
    public void handle(String eventType, String eventId, StripeObject stripeObject) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        
        // Use eventType from the parameter, not from getObject()
        switch (eventType) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(eventId, paymentIntent);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(eventId, paymentIntent);
                break;
            case "payment_intent.created":
                handlePaymentIntentCreated(eventId, paymentIntent);
                break;
            case "payment_intent.canceled":
                handlePaymentIntentCanceled(eventId, paymentIntent);
                break;
            case "payment_intent.requires_action":
                handlePaymentIntentRequiresAction(eventId, paymentIntent);
                break;
            default:
                logger.info("Unhandled PaymentIntent event: {}", eventId);
        }
    }
    
    private void handlePaymentIntentSucceeded(String eventId, PaymentIntent paymentIntent) {
        logger.info("PaymentIntent succeeded - eventId: {}, amount: {}, currency: {}", 
            eventId, paymentIntent.getAmount(), paymentIntent.getCurrency());
        
        // Try to find order by metadata.orderId first (new flow)
        String orderIdFromMetadata = paymentIntent.getMetadata().get("orderId");
        
        if (orderIdFromMetadata != null && !orderIdFromMetadata.startsWith("pending-")) {
            // This is an order created through our new API flow
            // Mark it as paid in MockNetSuite
            Order updatedOrder = netSuiteService.markOrderAsPaid(paymentIntent.getId());
            
            if (updatedOrder != null) {
                logger.info("PaymentIntent succeeded - eventId: {}, mockOrderId: {}, amount: {}, currency: {}, status: {}",
                    eventId, updatedOrder.getId(), updatedOrder.getAmount(), updatedOrder.getCurrency(), updatedOrder.getStatus());
            } else {
                logger.error("Failed to find order for PaymentIntent: {}", paymentIntent.getId());
            }
        } else {
            // Fallback to the original flow (for backward compatibility)
            // Create order in Mock NetSuite
            OrderDto orderDto = new OrderDto();
            orderDto.setStripePaymentId(paymentIntent.getId());
            orderDto.setStripeCustomerId(paymentIntent.getCustomer());
            orderDto.setAmount(paymentIntent.getAmount());
            orderDto.setCurrency(paymentIntent.getCurrency());
            orderDto.setDescription("PaymentIntent payment for " + paymentIntent.getId());
            orderDto.setStatus("completed");
            
            Order order = netSuiteService.createOrder(orderDto);
            
            if (order != null) {
                logger.info("PaymentIntent succeeded (fallback) - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                    eventId, order.getId(), order.getAmount(), order.getCurrency());
            } else {
                logger.error("Failed to create order in Mock NetSuite for PaymentIntent: {}", paymentIntent.getId());
            }
        }
    }
    
    private void handlePaymentIntentFailed(String eventId, PaymentIntent paymentIntent) {
        String failureMessage = paymentIntent.getLastPaymentError() != null 
            ? paymentIntent.getLastPaymentError().getMessage() 
            : "Unknown error";
        
        logger.info("PaymentIntent failed - eventId: {}, reason: {}", 
            eventId, failureMessage);
        
        // Try to find existing order and update status
        Order existingOrder = findOrderByStripePaymentId(paymentIntent.getId());
        
        if (existingOrder != null) {
            Order updatedOrder = netSuiteService.updateOrderStatus(existingOrder.getId(), "failed");
            logger.info("PaymentIntent failed - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                eventId, updatedOrder.getId(), updatedOrder.getAmount(), updatedOrder.getCurrency());
        } else {
            // Create a failed order record
            OrderDto orderDto = new OrderDto();
            orderDto.setStripePaymentId(paymentIntent.getId());
            orderDto.setStripeCustomerId(paymentIntent.getCustomer());
            orderDto.setAmount(paymentIntent.getAmount());
            orderDto.setCurrency(paymentIntent.getCurrency());
            orderDto.setDescription("Failed PaymentIntent: " + failureMessage);
            orderDto.setStatus("failed");
            
            Order order = netSuiteService.createOrder(orderDto);
            
            if (order != null) {
                logger.info("PaymentIntent failed - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                    eventId, order.getId(), order.getAmount(), order.getCurrency());
            }
        }
    }
    
    /**
     * Find an order by Stripe payment ID.
     * This is a simple search through available orders.
     */
    private Order findOrderByStripePaymentId(String stripePaymentId) {
        // This would typically query NetSuite or a local cache
        // For mock purposes, we log and return null
        logger.debug("Looking for order with Stripe payment ID: {}", stripePaymentId);
        return null;
    }
    
    private void handlePaymentIntentCreated(String eventId, PaymentIntent paymentIntent) {
        logger.info("PaymentIntent created - eventId: {}", eventId);
        
        // TODO: Handle new payment intent if needed
    }
    
    private void handlePaymentIntentCanceled(String eventId, PaymentIntent paymentIntent) {
        logger.info("PaymentIntent canceled - eventId: {}", eventId);
        
        // TODO: Handle canceled payment in NetSuite
    }
    
    private void handlePaymentIntentRequiresAction(String eventId, PaymentIntent paymentIntent) {
        logger.info("PaymentIntent requires action - eventId: {}", eventId);
        
        // TODO: Handle payment that requires additional action (e.g., 3D Secure)
    }
}

