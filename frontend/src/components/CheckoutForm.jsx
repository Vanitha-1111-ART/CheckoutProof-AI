import React from 'react';

export default function CheckoutForm({
  customerName,
  onCustomerNameChange,
  onSubmitOrder,
  errorMessage,
  isSubmitting,
  cartItemCount,
}) {
  const handleSubmit = (e) => {
    e.preventDefault();
    // Safety check against rapid click: if already submitting, ignore!
    if (isSubmitting) return;
    onSubmitOrder();
  };

  return (
    <form
      className="checkout-form-card"
      onSubmit={handleSubmit}
      noValidate
      data-testid="checkout-form"
    >
      <div className="form-header">
        <h3>Customer Details</h3>
        <span className="required-legend">* Indicates required field</span>
      </div>

      {/* Validation / Error Banner */}
      {errorMessage && (
        <div
          className="error-banner"
          role="alert"
          data-testid="error-message-banner"
        >
          <span className="error-icon">⚠️</span>
          <div className="error-content">
            <strong className="error-title">Validation Error</strong>
            <p className="error-text" data-testid="error-message-text">
              {errorMessage}
            </p>
          </div>
        </div>
      )}

      {/* Customer Name Input (preserves value on validation failure) */}
      <div className="form-group">
        <label htmlFor="customer-name" className="form-label">
          Customer Full Name <span className="asterisk">*</span>
        </label>
        <input
          id="customer-name"
          type="text"
          className={`form-input ${errorMessage && !customerName.trim() ? 'input-error' : ''}`}
          placeholder="e.g. Alex Johnson"
          value={customerName}
          onChange={(e) => onCustomerNameChange(e.target.value)}
          disabled={isSubmitting}
          autoComplete="name"
          data-testid="customer-name-input"
        />
        <span className="input-helper">
          Order verification receipt and confirmation will be addressed to this name.
        </span>
      </div>

      {/* Simulated Payment Notice (Clarifies no real gateway) */}
      <div className="payment-notice">
        <span className="notice-icon">🔒</span>
        <div className="notice-text">
          <strong>Demo Checkout Mode</strong>
          <span>No credit card required. Simulated instant verification for SDET testing.</span>
        </div>
      </div>

      {/* Place Order Button with Rapid Click / Duplicate Order Prevention */}
      <div className="form-actions">
        <button
          type="submit"
          className="btn btn-primary place-order-btn"
          disabled={isSubmitting}
          data-testid="place-order-btn"
        >
          {isSubmitting ? (
            <span className="loading-state" data-testid="order-submitting-state">
              <span className="spinner" /> Processing Order...
            </span>
          ) : (
            <span>Place Order Securely</span>
          )}
        </button>
      </div>

      <div className="guarantee-footer">
        <span className="guarantee-badge">🛡️ CheckoutProof AI Verified</span>
        <span className="guarantee-text">State consistency validated per transaction</span>
      </div>
    </form>
  );
}
