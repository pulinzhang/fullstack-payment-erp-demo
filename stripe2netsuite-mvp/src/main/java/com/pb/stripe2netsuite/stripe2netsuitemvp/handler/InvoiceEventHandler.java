package com.pb.stripe2netsuite.stripe2netsuitemvp.handler;

import com.pb.stripe2netsuite.stripe2netsuitemvp.model.Order;
import com.pb.stripe2netsuite.stripe2netsuitemvp.model.OrderDto;
import com.pb.stripe2netsuite.stripe2netsuitemvp.service.NetSuiteService;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import org.springframework.stereotype.Component;

/**
 * Handler for Invoice-related webhook events.
 */
@Component
public class InvoiceEventHandler extends AbstractStripeEventHandler {
    
    private final NetSuiteService netSuiteService;
    
    public InvoiceEventHandler(NetSuiteService netSuiteService) {
        this.netSuiteService = netSuiteService;
    }
    @Override
    public String[] getSupportedEventTypes() {
        return new String[] {
            "invoice.created",
            "invoice.finalized",
            "invoice.paid",
            "invoice.payment_failed",
            "invoice.voided",
            "invoice.deleted",
            "invoice.marked_uncollectible",
            "invoice.payment_action_required"
        };
    }
    
    @Override
    public void handle(String eventType, String eventId, StripeObject stripeObject) {
        Invoice invoice = (Invoice) stripeObject;
        
        // Use eventType from the parameter, not from getObject()
        switch (eventType) {
            case "invoice.created":
                handleInvoiceCreated(eventId, invoice);
                break;
            case "invoice.finalized":
                handleInvoiceFinalized(eventId, invoice);
                break;
            case "invoice.paid":
                handleInvoicePaid(eventId, invoice);
                break;
            case "invoice.payment_failed":
                handleInvoicePaymentFailed(eventId, invoice);
                break;
            case "invoice.voided":
                handleInvoiceVoided(eventId, invoice);
                break;
            case "invoice.deleted":
                handleInvoiceDeleted(eventId, invoice);
                break;
            case "invoice.payment_action_required":
                handleInvoicePaymentActionRequired(eventId, invoice);
                break;
            default:
                logger.info("Unhandled Invoice event: {}", eventId);
        }
    }
    
    private void handleInvoiceCreated(String eventId, Invoice invoice) {
        logger.info("Invoice created - eventId: {}, total: {}, customer: {}", 
            eventId, invoice.getTotal(), invoice.getCustomer());
        
        // Create a pending order for the invoice in Mock NetSuite
        OrderDto orderDto = new OrderDto();
        orderDto.setStripePaymentId(invoice.getId());
        orderDto.setStripeCustomerId(invoice.getCustomer());
        orderDto.setAmount(invoice.getTotal());
        orderDto.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency() : "usd");
        orderDto.setDescription("Invoice: " + invoice.getNumber());
        orderDto.setStatus("pending");
        
        Order order = netSuiteService.createOrder(orderDto);
        
        if (order != null) {
            logger.info("Invoice created - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                eventId, order.getId(), order.getAmount(), order.getCurrency());
        }
    }
    
    private void handleInvoiceFinalized(String eventId, Invoice invoice) {
        logger.info("Invoice finalized - eventId: {}, total: {}", 
            eventId, invoice.getTotal());
        
        // Update order status to finalized
        // For mock purposes, we create a new order record
        OrderDto orderDto = new OrderDto();
        orderDto.setStripePaymentId(invoice.getId());
        orderDto.setStripeCustomerId(invoice.getCustomer());
        orderDto.setAmount(invoice.getTotal());
        orderDto.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency() : "usd");
        orderDto.setDescription("Finalized Invoice: " + invoice.getNumber());
        orderDto.setStatus("finalized");
        
        Order order = netSuiteService.createOrder(orderDto);
        
        if (order != null) {
            logger.info("Invoice finalized - eventId: {}, mockOrderId: {}, amount: {}",
                eventId, order.getId(), order.getAmount());
        }
    }
    
    private void handleInvoicePaid(String eventId, Invoice invoice) {
        logger.info("Invoice paid - eventId: {}, total: {}, subscription: {}", 
            eventId, invoice.getTotal(), invoice.getSubscription());
        
        // Create order with completed status
        OrderDto orderDto = new OrderDto();
        orderDto.setStripePaymentId(invoice.getId());
        orderDto.setStripeCustomerId(invoice.getCustomer());
        orderDto.setAmount(invoice.getTotal());
        orderDto.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency() : "usd");
        orderDto.setDescription("Paid Invoice: " + invoice.getNumber());
        orderDto.setStatus("completed");
        
        Order order = netSuiteService.createOrder(orderDto);
        
        if (order != null) {
            logger.info("Invoice paid - eventId: {}, mockOrderId: {}, amount: {}, currency: {}",
                eventId, order.getId(), order.getAmount(), order.getCurrency());
        }
    }
    
    private void handleInvoicePaymentFailed(String eventId, Invoice invoice) {
        logger.info("Invoice payment failed - eventId: {}, customer: {}", 
            eventId, invoice.getCustomer());
        
        // Create order with failed status
        OrderDto orderDto = new OrderDto();
        orderDto.setStripePaymentId(invoice.getId());
        orderDto.setStripeCustomerId(invoice.getCustomer());
        orderDto.setAmount(invoice.getTotal());
        orderDto.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency() : "usd");
        orderDto.setDescription("Failed Invoice: " + invoice.getNumber());
        orderDto.setStatus("failed");
        
        Order order = netSuiteService.createOrder(orderDto);
        
        if (order != null) {
            logger.info("Invoice payment failed - eventId: {}, mockOrderId: {}, amount: {}",
                eventId, order.getId(), order.getAmount());
        }
    }
    
    private void handleInvoiceVoided(String eventId, Invoice invoice) {
        logger.info("Invoice voided - eventId: {}", eventId);
        
        // TODO: Handle voided invoice in NetSuite
    }
    
    private void handleInvoiceDeleted(String eventId, Invoice invoice) {
        logger.info("Invoice deleted - eventId: {}", eventId);
        
        // TODO: Handle deleted invoice in NetSuite
    }
    
    private void handleInvoicePaymentActionRequired(String eventId, Invoice invoice) {
        logger.info("Invoice payment action required - eventId: {}", eventId);
        
        // TODO: Handle invoice requiring additional payment action (e.g., 3D Secure)
    }
}

