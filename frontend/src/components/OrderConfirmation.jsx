import React from 'react';

export default function OrderConfirmation({ order, onResetOrder }) {
  return (
    <div className="confirmation-card" data-testid="order-confirmation-screen">
      <div className="confirmation-badge-icon">✅</div>

      <div className="confirmation-header">
        <h2 className="confirmation-title">Order Verified & Confirmed!</h2>
        <p className="confirmation-subtitle">
          Thank you, <strong data-testid="order-customer-name">{order.customerName}</strong>. 
          Your checkout transaction passed all verification checks.
        </p>
      </div>

      <div className="reference-box" data-testid="reference-box">
        <span className="ref-label">Order Reference Number</span>
        <div className="ref-number" data-testid="order-reference-number">
          {order.referenceNumber}
        </div>
        <span className="ref-timestamp">
          Timestamp: {new Date(order.timestamp).toLocaleString()}
        </span>
      </div>

      <div className="order-receipt">
        <h4>Items Purchased</h4>
        <div className="receipt-list" data-testid="receipt-items-list">
          {order.items.map((item) => (
            <div key={item.id} className="receipt-row">
              <span className="receipt-item-title">
                {item.quantity} × {item.name}
              </span>
              <span className="receipt-item-price">
                ${(item.price * item.quantity).toFixed(2)}
              </span>
            </div>
          ))}
        </div>

        <div className="receipt-divider" />

        <div className="receipt-total-row">
          <span>Amount Paid</span>
          <strong className="receipt-total-value" data-testid="order-final-total">
            ${order.total.toFixed(2)}
          </strong>
        </div>
      </div>

      <div className="confirmation-actions">
        <button
          type="button"
          className="btn btn-primary start-new-order-btn"
          onClick={onResetOrder}
          data-testid="start-new-order-btn"
        >
          ← Start New Order
        </button>
      </div>
    </div>
  );
}
