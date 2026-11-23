import React, { useState } from 'react';
import ProductList from './components/ProductList';
import CartSummary from './components/CartSummary';
import CheckoutForm from './components/CheckoutForm';
import OrderConfirmation from './components/OrderConfirmation';
import './App.css';

// Requirement 1: Two synthetic products with fixed prices
const INITIAL_PRODUCTS = [
  {
    id: 1,
    name: 'DevOps Cloud Pass',
    price: 49.00,
    badge: 'Popular',
    icon: '☁️',
    description: 'High-speed cloud test runners with parallel execution and CI/CD integration.',
  },
  {
    id: 2,
    name: 'SDET Automation Toolkit',
    price: 29.00,
    badge: 'Essential',
    icon: '⚡',
    description: 'Complete TestNG + Selenium framework template with locator healing & listeners.',
  },
];

export default function App() {
  const [products] = useState(INITIAL_PRODUCTS);
  const [cart, setCart] = useState([]);
  const [customerName, setCustomerName] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [confirmedOrder, setConfirmedOrder] = useState(null);

  // Requirement 2: Add product to cart
  const handleAddToCart = (product) => {
    setCart((prevCart) => {
      const existing = prevCart.find((item) => item.id === product.id);
      if (existing) {
        return prevCart.map((item) =>
          item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...prevCart, { ...product, quantity: 1 }];
    });
    // Clear cart-empty error if user adds a product
    if (errorMessage.includes('cart is empty')) {
      setErrorMessage('');
    }
  };

  // Requirement 3: Increase product quantity
  const handleIncrement = (productId) => {
    setCart((prevCart) =>
      prevCart.map((item) =>
        item.id === productId ? { ...item, quantity: item.quantity + 1 } : item
      )
    );
  };

  // Requirement 3: Decrease product quantity (removes if quantity reaches 0)
  const handleDecrement = (productId) => {
    setCart((prevCart) =>
      prevCart
        .map((item) =>
          item.id === productId ? { ...item, quantity: item.quantity - 1 } : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  // Requirement 2: Remove product entirely from cart
  const handleRemove = (productId) => {
    setCart((prevCart) => prevCart.filter((item) => item.id !== productId));
  };

  // Requirement 5: Automatically calculate the final order total
  const orderTotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  // Requirement 10 & 11: Validation without losing entered info
  const handleCustomerNameChange = (name) => {
    setCustomerName(name);
    // Clear customer name error once user begins typing
    if (errorMessage.includes('Customer Name') && name.trim().length > 0) {
      setErrorMessage('');
    }
  };

  // Generate a distinct, verifiable reference number
  const generateReferenceId = () => {
    const randomDigits = Math.floor(100000 + Math.random() * 900000);
    return `CP-${randomDigits}`;
  };

  // Requirement 7, 8, 9, 10, 11, 12, 13: Order placement & validation
  const handlePlaceOrder = () => {
    // Requirement 13: Prevent duplicate orders if already submitting
    if (isSubmitting) return;

    // Requirement 8: Prevent checkout when cart is empty
    if (cart.length === 0) {
      setErrorMessage('Your cart is empty. Please add at least one product before placing an order.');
      return;
    }

    // Requirement 9: Prevent checkout when customer name is blank
    if (!customerName || !customerName.trim()) {
      setErrorMessage('Customer Name is required. Please enter your name to proceed.');
      return;
    }

    // Lock submission immediately to prevent duplicate orders
    setIsSubmitting(true);
    setErrorMessage('');

    // Simulate reliable transaction processing
    setTimeout(() => {
      const newOrder = {
        referenceNumber: generateReferenceId(),
        customerName: customerName.trim(),
        items: [...cart],
        total: orderTotal,
        timestamp: new Date().toISOString(),
      };

      setConfirmedOrder(newOrder);
      setIsSubmitting(false);
    }, 400);
  };

  // Reset order to initial state
  const handleResetOrder = () => {
    setCart([]);
    setCustomerName('');
    setErrorMessage('');
    setConfirmedOrder(null);
  };

  return (
    <div className="app-container" data-testid="checkoutproof-app">
      {/* Top Header & Branding */}
      <header className="app-header">
        <div className="header-content">
          <div className="brand">
            <span className="brand-icon">🛒</span>
            <div>
              <h1 className="brand-title">CheckoutProof AI</h1>
              <p className="brand-tagline">Reliable Checkout. Verified Every Time.</p>
            </div>
          </div>
          <div className="header-badges">
            <span className="badge-sdet">SDET Test-Ready App</span>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="main-content">
        {confirmedOrder ? (
          /* Requirement 12: Order Confirmation Screen */
          <OrderConfirmation
            order={confirmedOrder}
            onResetOrder={handleResetOrder}
          />
        ) : (
          /* Main 2-Column Checkout Layout */
          <div className="checkout-layout">
            {/* Left Column: Product Catalog */}
            <div className="layout-column catalog-column">
              <ProductList
                products={products}
                cart={cart}
                onAddToCart={handleAddToCart}
                onIncrement={handleIncrement}
                onDecrement={handleDecrement}
              />
            </div>

            {/* Right Column: Cart Summary & Checkout Form */}
            <div className="layout-column checkout-column">
              <CartSummary
                cart={cart}
                onIncrement={handleIncrement}
                onDecrement={handleDecrement}
                onRemove={handleRemove}
                orderTotal={orderTotal}
              />

              <CheckoutForm
                customerName={customerName}
                onCustomerNameChange={handleCustomerNameChange}
                onSubmitOrder={handlePlaceOrder}
                errorMessage={errorMessage}
                isSubmitting={isSubmitting}
                cartItemCount={cart.length}
              />
            </div>
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="app-footer">
        <p>CheckoutProof AI &bull; Built for automated browser validation and AI-assisted triage.</p>
      </footer>
    </div>
  );
}
