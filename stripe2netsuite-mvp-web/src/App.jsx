import React, { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';

// API Base URL - Using Vite proxy or direct
const API_BASE = '/api';

// Get publishable key from backend
async function getPublishableKey() {
  const response = await fetch(`${API_BASE}/orders/config`);
  const data = await response.json();
  return data.publishableKey;
}

// Create order on backend
async function createOrder(amount, currency, description) {
  const response = await fetch(`${API_BASE}/orders/create`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      amount: Math.round(amount * 100), // Convert to cents
      currency,
      description,
    }),
  });
  
  if (!response.ok) {
    throw new Error('Failed to create order');
  }
  
  return response.json();
}

// Get order from backend
async function getOrder(orderId) {
  const response = await fetch(`${API_BASE}/mock/orders/${orderId}`);
  if (!response.ok) {
    return null;
  }
  return response.json();
}

// Stripe promise (will be initialized later)
let stripePromise = null;

function CheckoutForm({ orderData, onComplete, onError }) {
  const stripe = useStripe();
  const elements = useElements();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setLoading(true);
    setError(null);

    const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(
      orderData.clientSecret,
      {
        payment_method: {
          card: elements.getElement(CardElement),
          billing_details: {
            name: 'Test Customer',
          },
        },
      }
    );

    setLoading(false);

    if (stripeError) {
      setError(stripeError.message);
      onError(stripeError.message);
    } else if (paymentIntent.status === 'succeeded') {
      onComplete(paymentIntent);
    }
  };

  const cardElementOptions = {
    style: {
      base: {
        fontSize: '16px',
        color: '#1a1a2e',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
        '::placeholder': {
          color: '#aab7c4',
        },
      },
      invalid: {
        color: '#fa755a',
        iconColor: '#fa755a',
      },
    },
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label>Card Details</label>
        <div className="stripe-element">
          <CardElement options={cardElementOptions} />
        </div>
      </div>
      
      {error && (
        <div className="result error">
          <h4>Error</h4>
          <p>{error}</p>
        </div>
      )}

      <button 
        type="submit" 
        className="btn btn-primary"
        disabled={!stripe || loading}
      >
        {loading ? 'Processing...' : `Pay $${(orderData.amount / 100).toFixed(2)}`}
      </button>
    </form>
  );
}

function App() {
  const [stripeKey, setStripeKey] = useState(null);
  const [amount, setAmount] = useState('99.99');
  const [description, setDescription] = useState('Test Order');
  const [currentStep, setCurrentStep] = useState(0);
  const [orderData, setOrderData] = useState(null);
  const [paymentResult, setPaymentResult] = useState(null);
  const [finalOrderStatus, setFinalOrderStatus] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  // Fetch Stripe publishable key on mount
  useEffect(() => {
    async function initStripe() {
      try {
        const key = await getPublishableKey();
        if (key) {
          stripePromise = loadStripe(key);
          setStripeKey(key);
        } else {
          setError('Failed to load Stripe configuration');
        }
      } catch (err) {
        setError('Failed to connect to backend');
      }
    }
    initStripe();
  }, []);

  const steps = [
    { title: 'Initialize', detail: 'Load Stripe configuration' },
    { title: 'Create Order', detail: 'Backend creates order & PaymentIntent' },
    { title: 'Enter Payment', detail: 'Customer enters card details' },
    { title: 'Process Payment', detail: 'Stripe processes payment' },
    { title: 'Webhook', detail: 'Backend updates order status' },
    { title: 'Complete', detail: 'Order synced to NetSuite' },
  ];

  const handleCreateOrder = async () => {
    setLoading(true);
    setError(null);
    setPaymentResult(null);
    setFinalOrderStatus(null);
    setCurrentStep(1);

    try {
      const order = await createOrder(
        parseFloat(amount),
        'usd',
        description
      );
      
      setOrderData(order);
      setCurrentStep(2);
    } catch (err) {
      setError(err.message);
      setCurrentStep(0);
    } finally {
      setLoading(false);
    }
  };

  const handlePaymentComplete = async (paymentIntent) => {
    setPaymentResult(paymentIntent);
    setCurrentStep(3);

    // Wait for webhook to process
    setTimeout(async () => {
      try {
        const order = await getOrder(orderData.orderId);
        if (order) {
          setFinalOrderStatus(order.status);
          setCurrentStep(5);
        } else {
          setCurrentStep(4);
        }
      } catch {
        setCurrentStep(4);
      }
    }, 2000);
  };

  const handlePaymentError = (errorMessage) => {
    setError(errorMessage);
    setCurrentStep(2);
  };

  const reset = () => {
    setCurrentStep(0);
    setOrderData(null);
    setPaymentResult(null);
    setFinalOrderStatus(null);
    setError(null);
  };

  return (
    <div className="container">
      <div className="header">
        <h1>Stripe2NetSuite Payment Demo</h1>
        <p>Complete payment flow with backend-driven PaymentIntent</p>
      </div>

      {/* Flow Steps */}
      <div className="card">
        <h2>Payment Flow (T0-T8)</h2>
        <div className="steps">
          {steps.map((step, index) => (
            <div 
              key={index} 
              className={`step ${
                index < currentStep ? 'completed' : 
                index === currentStep ? 'active' : ''
              }`}
            >
              <div className="step-number">
                {index < currentStep ? '✓' : index + 1}
              </div>
              <div className="step-content">
                <div className="step-title">{step.title}</div>
                <div className="step-detail">{step.detail}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Error Display */}
      {error && (
        <div className="card">
          <div className="result error">
            <h4>Error</h4>
            <p>{error}</p>
          </div>
        </div>
      )}

      {/* Order Creation Form */}
      {currentStep === 0 && (
        <div className="card">
          <h2>Step 1: Create Order</h2>
          <div className="form-group">
            <label>Amount (USD)</label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              min="1"
              step="0.01"
            />
          </div>
          <div className="form-group">
            <label>Description</label>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>
          <button 
            className="btn btn-primary"
            onClick={handleCreateOrder}
            disabled={loading || !stripeKey}
          >
            {loading ? 'Creating Order...' : 'Create Order & PaymentIntent'}
          </button>
          {!stripeKey && (
            <p style={{ marginTop: '1rem', color: '#666', fontSize: '0.9rem' }}>
              Loading Stripe configuration...
            </p>
          )}
        </div>
      )}

      {/* Order Summary */}
      {orderData && (
        <div className="card">
          <h2>Order Summary</h2>
          <div className="order-summary">
            <div className="order-summary-item">
              <span className="order-summary-label">Order ID</span>
              <span className="order-summary-value">{orderData.orderId}</span>
            </div>
            <div className="order-summary-item">
              <span className="order-summary-label">Amount</span>
              <span className="order-summary-value">
                ${(orderData.amount / 100).toFixed(2)} {orderData.currency.toUpperCase()}
              </span>
            </div>
            <div className="order-summary-item">
              <span className="order-summary-label">Status</span>
              <span className="order-summary-value">{orderData.status}</span>
            </div>
            <div className="order-summary-item">
              <span className="order-summary-label">PaymentIntent ID</span>
              <span className="order-summary-value" style={{ fontSize: '0.8rem' }}>
                {orderData.paymentIntentId}
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Payment Form */}
      {currentStep === 2 && orderData && stripePromise && (
        <div className="card">
          <h2>Step 2: Enter Payment Details</h2>
          <Elements stripe={stripePromise}>
            <CheckoutForm
              orderData={orderData}
              onComplete={handlePaymentComplete}
              onError={handlePaymentError}
            />
          </Elements>
        </div>
      )}

      {/* Payment Result */}
      {paymentResult && (
        <div className="card">
          <h2>Payment Result</h2>
          <div className="result">
            <h4>Payment {paymentResult.status}</h4>
            <pre>{JSON.stringify(paymentResult, null, 2)}</pre>
          </div>
        </div>
      )}

      {/* Final Order Status */}
      {finalOrderStatus && (
        <div className="card">
          <h2>Final Order Status</h2>
          <div className="result">
            <h4>Order Status: {finalOrderStatus.toUpperCase()}</h4>
            <p>The order has been successfully synced to NetSuite!</p>
          </div>
          <button 
            className="btn btn-secondary" 
            onClick={reset}
            style={{ marginTop: '1rem' }}
          >
            Start New Order
          </button>
        </div>
      )}
    </div>
  );
}

export default App;

