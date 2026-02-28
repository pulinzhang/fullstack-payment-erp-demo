# Stripe to NetSuite Integration MVP

A Spring Boot application that receives Stripe webhooks and syncs data to NetSuite.

---

## Features

- **Stripe Webhook Handling**: Process events from Stripe (payments, customers, invoices, subscriptions)
- **NetSuite Integration**: Sync Stripe data to NetSuite using Token-Based Authentication (TBA)
- **Mock NetSuite Mode**: Full mock implementation for development and demonstration (no NetSuite credentials required)
- **Event-Driven Architecture**: Extensible handler system for different event types
- **OpenAPI Documentation**: Swagger UI for API testing
- **Signature Verification**: Secure webhook endpoint with Stripe signature verification

---

## Quick Start (Mock Mode)

The application now includes a **Mock NetSuite Service** that simulates NetSuite operations without requiring real NetSuite credentials. This is perfect for:
- Local development and testing
- Demonstrations and portfolio showcases
- CI/CD testing pipelines

### 1. Run the Application

```bash
mvn spring-boot:run
```

### 2. Test with Stripe CLI

```bash
# Start Stripe CLI listener
stripe listen --forward-to localhost:8080/webhook/stripe

# Trigger different events
stripe trigger payment_intent.succeeded
stripe trigger charge.succeeded
stripe trigger charge.failed
stripe trigger invoice.paid
stripe trigger invoice.payment_failed
```

### 3. View Results

- **Console Logs**: See the full workflow from Stripe event → Mock NetSuite sync
- **Mock API Endpoints**: 
  - GET `/mock/orders` - View all mock orders
  - GET `/mock/orders/{id}` - View specific order
  - GET `/mock/customers` - View all mock customers
  - GET `/mock/customers/{id}` - View specific customer
  - GET `/mock/health` - Service health check
  - DELETE `/mock/data` - Reset mock data

### 4. Console Logging Format

Each event logs the complete workflow:

```
PaymentIntent succeeded - eventId: evt_XXX, mockOrderId: MOCK-ORDER-001, amount: 2000, currency: usd
[MockNetSuite] action: create, eventId: pi_XXX, orderId: MOCK-ORDER-001, amount: 2000, currency: usd, status: completed
```

Charge events:
```
Charge succeeded - eventId: evt_XXX, mockOrderId: MOCK-ORDER-002, amount: 5000, currency: usd
[MockNetSuite] action: create, eventId: ch_XXX, orderId: MOCK-ORDER-002, amount: 5000, currency: usd, status: completed
```

Invoice events:
```
Invoice paid - eventId: evt_XXX, mockOrderId: MOCK-ORDER-003, amount: 10000, currency: usd
[MockNetSuite] action: create, eventId: in_XXX, orderId: MOCK-ORDER-003, amount: 10000, currency: usd, status: completed
```

### 4. Swagger UI

Access the interactive API documentation at: http://localhost:8080/swagger-ui.html

---

## Demo: Complete Payment Flow (T0-T8)

This demo showcases a complete Stripe payment flow from order creation to ERP synchronization.

### Flow Overview

| Step | Description | Backend Action |
|------|-------------|----------------|
| T0 | User places order | Frontend sends order request |
| T1 | Backend creates order (PENDING) | Creates order in Mock NetSuite |
| T2 | Backend creates PaymentIntent | Calls Stripe API with metadata |
| T3 | Frontend initiates payment | Uses clientSecret with Stripe.js |
| T4 | Stripe processes payment | Payment processed by Stripe |
| T5 | Stripe sends webhook | Event sent to `/webhook/stripe` |
| T6 | Backend verifies signature | Validates Stripe-Signature header |
| T7 | Update order status = PAID | Updates order in Mock NetSuite |
| T8 | Call ERP | Syncs to NetSuite (simulated) |

### API Endpoints

#### 1. Create Order with PaymentIntent
```
POST /api/orders/create
```

Request:
```json
{
  "amount": 9999,
  "currency": "usd",
  "description": "Test order"
}
```

Response (HTTP 201 Created):
```json
{
  "orderId": "MOCK-ORDER-002",
  "status": "pending",
  "amount": 9999,
  "currency": "usd",
  "clientSecret": "pi_xxx_secret_xxx",
  "paymentIntentId": "pi_xxx"
}
```

#### 2. Get Stripe Publishable Key
```
GET /api/orders/config
```

Response:
```json
{
  "publishableKey": "pk_test_xxx"
}
```

### Frontend Implementation

```javascript
// 1. Call backend to create order and PaymentIntent
const response = await fetch('/api/orders/create', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({amount: 9999, currency: 'usd', description: 'Test order'})
});
const {orderId, clientSecret} = await response.json();

// 2. Initialize Stripe and confirm payment
const stripe = Stripe(publishableKey);
const {error, paymentIntent} = await stripe.confirmCardPayment(clientSecret, {
  payment_method: {
    card: cardElement,
    billing_details: {name: 'Customer Name'}
  }
});

if (error) {
  console.error('Payment failed:', error.message);
} else if (paymentIntent.status === 'succeeded') {
  console.log('Payment succeeded! Order ID:', orderId);
}
```

### Testing the Full Flow

```bash
# 1. Start the application
mvn spring-boot:run

# 2. Start Stripe CLI listener (in another terminal)
stripe listen --forward-to localhost:8080/webhook/stripe

# 3. Create an order (this creates PaymentIntent)
curl -X POST http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '{"amount": 9999, "currency": "usd", "description": "Demo order"}'

# 4. Check order status (should be "pending")
curl http://localhost:8080/mock/orders

# 5. After payment, Stripe sends webhook and order status updates to "paid"
# Check the updated status
curl http://localhost:8080/mock/orders/MOCK-ORDER-002

# Expected: {"id": "MOCK-ORDER-002", "status": "paid", ...}
```

### Key Design Principles

1. **Backend-centric Stripe Logic**: All Stripe API calls (PaymentIntent creation) happen on the backend
2. **Security**: Webhook signature verification ensures authenticity
3. **Metadata Tracking**: PaymentIntent includes `orderId` in metadata for correlation
4. **Separation of Concerns**: Controllers handle HTTP, Services handle business logic

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Stripe Java SDK 27.0.0**
- **Apache HttpClient 5** (NetSuite REST API)
- **SpringDoc OpenAPI** (Swagger UI)

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Stripe API Keys
stripe.api.key=sk_test_xxx
stripe.publishable.key=pk_test_xxx

# Stripe Webhook Secret
stripe.webhook.secret=whsec_xxx

# NetSuite Configuration
netsuite.account-id=YOUR_ACCOUNT_ID
netsuite.consumer-key=YOUR_CONSUMER_KEY
netsuite.consumer-secret=YOUR_CONSUMER_SECRET
netsuite.token-id=YOUR_TOKEN_ID
netsuite.token-secret=YOUR_TOKEN_SECRET
netsuite.base-url=https://YOUR_ACCOUNT_ID.suitetalk.api.netsuite.com

# Mock NetSuite Configuration (Development Mode)
netsuite.mock.enabled=true
netsuite.mock.seed-data=true
netsuite.mock.log-all-calls=true
```

### Mock Mode Configuration

By default, the Mock NetSuite service is enabled. To disable it and use real NetSuite:

```properties
netsuite.mock.enabled=false
```

When mock is enabled:
- All operations are logged to console in format: `[MockNetSuite] action: create/update/get, eventId: XXX, orderId: XXX`
- Orders and customers are stored in-memory
- Sample data is initialized on startup

---

## Local Development

### Option 1: Using ngrok (Recommended for Stripe Dashboard)

#### 1. Start Spring Boot

```bash
mvn spring-boot:run
```

#### 2. Start ngrok

```bash
ngrok http 8080
```

Copy your ngrok URL: `https://xxx.ngrok-free.dev`

#### 3. Configure Stripe Dashboard

1. Go to **Stripe Dashboard** → Developers → Webhooks
2. Add endpoint: `https://xxx.ngrok-free.dev/api/webhook`
3. Select events to listen
4. Save

#### 4. Test

Create a test payment in Stripe or use Stripe CLI:

```bash
stripe trigger payment_intent.succeeded
```

---

### Option 2: Using Stripe CLI Only (Local Testing)

#### 1. Start Spring Boot

```bash
mvn spring-boot:run
```

#### 2. Start Stripe CLI Listener

```bash
stripe listen --forward-to localhost:8080/api/webhook
```

Copy the webhook signing secret displayed by the CLI.

#### 3. Update Configuration

Add the secret to `application.properties`:

```properties
stripe.webhook.secret=whsec_xxx
```

#### 4. Trigger Test Events

```bash
stripe trigger payment_intent.succeeded
stripe trigger customer.created
stripe trigger invoice.paid
```

---

## API Documentation

After starting the application:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

---

## Supported Event Types

| Event Type | Handler |
|-----------|---------|
| `charge.succeeded` | ChargeEventHandler |
| `charge.failed` | ChargeEventHandler |
| `charge.refunded` | ChargeEventHandler |
| `payment_intent.succeeded` | PaymentIntentEventHandler |
| `payment_intent.payment_failed` | PaymentIntentEventHandler |
| `customer.created` | CustomerEventHandler |
| `customer.updated` | CustomerEventHandler |
| `customer.deleted` | CustomerEventHandler |
| `invoice.paid` | InvoiceEventHandler |
| `invoice.payment_failed` | InvoiceEventHandler |
| `subscription.created` | SubscriptionEventHandler |
| `subscription.updated` | SubscriptionEventHandler |
| `subscription.deleted` | SubscriptionEventHandler |

---

## Project Structure

```
src/main/java/com/pb/stripe2netsuite/stripe2netsuitemvp/
├── config/
│   ├── NetSuiteConfig.java          # NetSuite TBA configuration
│   ├── OpenApiConfig.java           # Swagger/OpenAPI config
│   ├── StripeConfig.java            # Stripe API client
│   └── StripeWebhookConfig.java     # Webhook security config
├── controller/
│   ├── StripeWebhookController.java # Stripe webhook endpoint
│   └── MockNetSuiteController.java  # Mock NetSuite demo endpoints
├── handler/
│   ├── AbstractStripeEventHandler.java
│   ├── ChargeEventHandler.java      # Handles charge.* events
│   ├── CustomerEventHandler.java
│   ├── InvoiceEventHandler.java     # Handles invoice.* events
│   ├── PaymentIntentEventHandler.java # Handles payment_intent.* events
│   ├── StripeEventHandler.java
│   └── SubscriptionEventHandler.java
├── model/
│   ├── Order.java                   # NetSuite Order model
│   ├── OrderDto.java                # Order data transfer object
│   └── Customer.java                # NetSuite Customer model
├── service/
│   ├── NetSuiteService.java         # NetSuite service interface
│   ├── MockNetSuiteService.java     # Mock implementation
│   ├── StripeEventParser.java
│   └── StripeEventProcessor.java
└── Stripe2netsuiteMvpApplication.java
```

---

## Portfolio & Demonstration Value

This project demonstrates the following skills for potential clients:

### Technical Skills
- **Spring Boot Development**: REST API creation, dependency injection, configuration management
- **Event-Driven Architecture**: Webhook handling, event processing pipelines
- **API Integration**: Stripe webhooks, NetSuite REST API
- **Security**: Webhook signature verification, token-based authentication
- **Testing & Debugging**: Local development with Stripe CLI

### Architecture Patterns
- **Interface-based Design**: `NetSuiteService` interface allows switching between mock and real implementations
- **Handler Pattern**: Extensible event handler system
- **Clean Code**: Separation of concerns (controllers, services, handlers)

### Business Value
- **Automated Reconciliation**: Stripe payments automatically synced to NetSuite
- **Real-time Processing**: Events processed as they occur
- **Error Handling**: Failed payments tracked and logged

### Ready for Production (with real NetSuite)
- Swap `MockNetSuiteService` for `NetSuiteServiceImpl` with real credentials
- Add database persistence for order history
- Implement retry logic for failed API calls
- Add authentication to mock endpoints

---

## Build

```bash
mvn clean package
```

---

## License

MIT

