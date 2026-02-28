package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.OrderDto;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.NetSuiteService;
import com.stripe.model.Charge;
import com.stripe.model.StripeObject;
import org.springframework.stereotype.Component;

/**
 * Handler for Charge-related webhook events.
 */
@Component
public class ChargeEventHandler extends AbstractStripeEventHandler {
    
    private final NetSuiteService netSuiteService;
    
    public ChargeEventHandler(NetSuiteService netSuiteService) {
        this.netSuiteService = netSuiteService;
    }
    @Override
    public String[] getSupportedEventTypes() {
        return new String[] {
            "charge.succeeded",
            "charge.failed",
            "charge.refunded",
            "charge.captured",
            "charge.updated",
            "charge.dispute.created"
        };
    }
    
    @Override
    public void handle(String eventType, String eventId, StripeObject stripeObject) {
        Charge charge = (Charge) stripeObject;
        
        // Use eventType from the parameter, not from getObject()
        switch (eventType) {
            case "charge.succeeded":
                handleChargeSucceeded(eventId, charge);
                break;
            case "charge.failed":
                handleChargeFailed(eventId, charge);
                break;
            case "charge.refunded":
                handleChargeRefunded(eventId, charge);
                break;
            case "charge.captured":
                handleChargeCaptured(eventId, charge);
                break;
            case "charge.updated":
                handleChargeUpdated(eventId, charge);
                break;
            default:
                logger.info("Unhandled Charge event: {}", eventId);
        }
    }
    
    private void handleChargeSucceeded(String eventId, Charge charge) {
        logger.info("Charge succeeded - eventId: {}, amount: {}, currency: {}", 
            eventId, charge.getAmount(), charge.getCurrency());
        
        // Create order in Mock NetSuite
        OrderDto orderDto = new OrderDto();
        orderDto.setStripePaymentId(charge.getId());
        orderDto.setStripeCustomerId(charge.getCustomer());
        orderDto.setAmount(charge.getAmount());
        orderDto.setCurrency(charge.getCurrency());
        orderDto.setDescription("Charge payment for " + charge.getId());
        orderDto.setStatus("completed");
        
        Order order = netSuiteService.createOrder(orderDto);
        
        if (order != null) {
            logger.info("Charge succeeded - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                eventId, order.getId(), order.getAmount(), order.getCurrency());
        } else {
            logger.error("Failed to create order in Mock NetSuite for Charge: {}", charge.getId());
        }
    }
    
    private void handleChargeFailed(String eventId, Charge charge) {
        logger.info("Charge failed - eventId: {}, reason: {}", 
            eventId, charge.getFailureMessage());
        
        // Create a failed order record
        OrderDto orderDto = new OrderDto();
        orderDto.setStripePaymentId(charge.getId());
        orderDto.setStripeCustomerId(charge.getCustomer());
        orderDto.setAmount(charge.getAmount());
        orderDto.setCurrency(charge.getCurrency());
        orderDto.setDescription("Failed Charge: " + charge.getFailureMessage());
        orderDto.setStatus("failed");
        
        Order order = netSuiteService.createOrder(orderDto);
        
        if (order != null) {
            logger.info("Charge failed - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                eventId, order.getId(), order.getAmount(), order.getCurrency());
        }
    }
    
    private void handleChargeRefunded(String eventId, Charge charge) {
        logger.info("Charge refunded - eventId: {}, amount: {}, refunded: {}", 
            eventId, charge.getAmount(), charge.getAmountRefunded());
        
        // Try to find existing order and update status
        Order existingOrder = findOrderByStripePaymentId(charge.getId());
        
        if (existingOrder != null) {
            Order updatedOrder = netSuiteService.updateOrderStatus(existingOrder.getId(), "refunded");
            logger.info("Charge refunded - eventId: {}, mockOrderId: {}, amount: {}, refundedAmount: {}",
                eventId, updatedOrder.getId(), charge.getAmount(), charge.getAmountRefunded());
        } else {
            logger.warn("Could not find order for refunded charge: {}", charge.getId());
        }
    }
    
    private void handleChargeCaptured(String eventId, Charge charge) {
        logger.info("Charge captured - eventId: {}", eventId);
        
        // TODO: Handle captured charge (for authorized-only charges)
    }
    
    private void handleChargeUpdated(String eventId, Charge charge) {
        logger.info("Charge updated - eventId: {}, amount: {}, status: {}", 
            eventId, charge.getAmount(), charge.getStatus());
        
        // TODO: Handle charge update in NetSuite
        // - Update payment record if details changed
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
}

