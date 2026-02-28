# Stripe to NetSuite Integration (Enterprise MVP)

A production-style full-stack MVP demonstrating secure payment orchestration between
Stripe and
Oracle NetSuite.

This project implements a complete payment lifecycle:

```
Frontend → Backend → Stripe
Stripe → Webhook → Backend
Backend → ERP (Mock or Real)
```

It follows enterprise-grade best practices for:

* Secure payment handling
* Webhook signature verification
* Idempotent event processing
* ERP synchronization
* Order lifecycle management

---

# 🚀 Architecture Overview

```
┌─────────────────┐
│   Frontend      │  React + Stripe Elements
│   (Vite)        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Backend       │  Spring Boot 3
│   (API Layer)   │
└────────┬────────┘
         │
         ├──────────────▶ Stripe API (PaymentIntent)
         │
         ◀────────────── Stripe Webhook Events
         │
         ▼
┌─────────────────┐
│   NetSuite      │  Mock or Real Integration
│   ERP Layer     │
└─────────────────┘
```

---

# 🧠 System Design Highlights

This project demonstrates real-world integration patterns:

* ✅ Backend-created PaymentIntent
* ✅ Secure use of publishable key on frontend
* ✅ Webhook signature validation
* ✅ Metadata-based order mapping
* ✅ Idempotent event handling
* ✅ Order state machine
* ✅ ERP abstraction layer
* ✅ Clean separation of concerns

---

# 📦 Order Lifecycle

```
PENDING → PAID → SYNCED_TO_ERP
```

| Status        | Description                           |
| ------------- | ------------------------------------- |
| PENDING       | Order created, awaiting payment       |
| PAID          | Stripe confirms successful payment    |
| SYNCED_TO_ERP | Order successfully pushed to NetSuite |

Only `payment_intent.succeeded` triggers ERP synchronization.

---

# 🗂 Project Structure

```
Stripe2NetSuite-MVP/
├── stripe2netsuite-mvp/         # Backend (Spring Boot)
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
│
├── stripe2netsuite-mvp-web/     # Frontend (React + Vite)
│   ├── src/
│   ├── package.json
│   └── index.html
│
└── README.md
```

---

# 🛠 Tech Stack

## Backend

* Java 17
* Spring Boot 3.2.x
* Stripe Java SDK
* RESTful API
* OpenAPI (Swagger)

## Frontend

* React 18
* Vite
* @stripe/react-stripe-js
* @stripe/stripe-js
* Stripe Elements (secure card input)

---

# ⚙️ Configuration

## Backend Configuration

Edit:

```
stripe2netsuite-mvp/src/main/resources/application.properties
```

```properties
# Stripe Secret Key (Server-side only)
stripe.api-key=sk_test_xxx

# Stripe Publishable Key (Safe for frontend)
stripe.publishable-key=pk_test_xxx

# Stripe Webhook Secret
stripe.webhook.secret=whsec_xxx

# NetSuite (Only needed for real integration)
netsuite.account-id=YOUR_ACCOUNT_ID
netsuite.consumer-key=YOUR_CONSUMER_KEY
netsuite.consumer-secret=YOUR_CONSUMER_SECRET
netsuite.token-id=YOUR_TOKEN_ID
netsuite.token-secret=YOUR_TOKEN_SECRET
```

---

# ▶️ Running the Application

## 1️⃣ Start Backend

```bash
cd stripe2netsuite-mvp
mvn spring-boot:run
```

Backend runs at:

```
http://localhost:8080
```

---

## 2️⃣ Start Frontend

```bash
cd stripe2netsuite-mvp-web
npm install
npm run dev
```

Frontend runs at:

```
http://localhost:5173
```

---

# 💳 Testing the Payment Flow

## Option 1 — Using Stripe CLI (Recommended)

Install Stripe CLI from:

[https://stripe.com/docs/stripe-cli](https://stripe.com/docs/stripe-cli)

Then run:

```bash
# Start backend
mvn spring-boot:run

# Forward webhooks
stripe listen --forward-to localhost:8080/webhook/stripe

# Start frontend
npm run dev
```

Use Stripe test card:

```
4242 4242 4242 4242
Any future expiry date
Any CVC
```

---

## Option 2 — Using ngrok + Stripe Dashboard

```bash
ngrok http 8080
```

Then configure webhook endpoint in Stripe Dashboard:

```
https://your-ngrok-url/webhook/stripe
```

---

# 📡 API Endpoints

| Method | Endpoint             | Description                   |
| ------ | -------------------- | ----------------------------- |
| POST   | `/api/orders/create` | Create order + PaymentIntent  |
| GET    | `/api/orders/config` | Return Stripe publishable key |
| POST   | `/webhook/stripe`    | Stripe webhook receiver       |
| GET    | `/mock/orders`       | List mock orders              |
| GET    | `/mock/orders/{id}`  | View order                    |
| GET    | `/mock/health`       | Health check                  |

---

# 🔐 Security Model

* Secret key never exposed to frontend
* Publishable key safe for browser usage
* Stripe-Signature header validation
* Webhook verification using endpoint secret
* Event-based idempotency via orderId metadata
* ERP update only after verified payment success

---

# 🏢 NetSuite Integration Layer

This project includes:

* `NetSuiteService` (interface)
* `MockNetSuiteService` (development)
* `RealNetSuiteService` (production-ready placeholder)

This allows seamless transition from development to real ERP integration.

---

# 🧩 Production Considerations

This MVP supports extension to:

* Redis-based temporary order reservation
* Retry-safe webhook processing
* Database persistence
* Real NetSuite Token-Based Authentication (TBA)
* Horizontal scaling
* Logging & monitoring integration
* Subscription or marketplace models

---

# 📊 Why This Architecture?

This design ensures:

* No duplicate ERP orders
* Payment confirmation handled server-side
* Clear audit trail
* Decoupled ERP logic
* Scalability
* Compliance with Stripe best practices

---

# 🎯 Use Cases

This architecture is suitable for:

* SaaS platforms
* B2B commerce
* Marketplace systems
* ERP-integrated billing systems
* Subscription services
* Enterprise order processing

---

# 📖 Swagger API Docs

After backend starts:

```
http://localhost:8080/swagger-ui.html
```

---

# 🏁 Summary

This project demonstrates enterprise-grade payment orchestration between Stripe and NetSuite ERP using modern full-stack technologies.

It is structured as a scalable, secure foundation for real-world financial system integration.

---

# 📜 License

MIT

---
