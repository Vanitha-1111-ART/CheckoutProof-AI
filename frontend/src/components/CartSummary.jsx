import React from 'react';

export default function CartSummary({ cart, onIncrement, onDecrement, onRemove, orderTotal }) {
  const isCartEmpty = cart.length === 0;

  return (
    <div className="cart-card" data-testid="cart-summary-section">
      <div className="cart-header">
        <h3>Order Summary</h3>
        <span className="cart-item-count" data-testid="cart-count-badge">
          {cart.reduce((sum, item) => sum + item.quantity, 0)} items
        </span>
      </div>

      {isCartEmpty ? (
        <div className="cart-empty-state" data-testid="cart-empty-message">
          <div className="empty-cart-icon">🛒</div>
          <p className="empty-text">Your cart is currently empty.</p>
          <span className="empty-hint">Add items from the product list to begin checkout.</span>
        </div>
      ) : (
        <div className="cart-items-list" data-testid="cart-items-list">
          {cart.map((item) => {
            const itemTotal = item.price * item.quantity;
            return (
              <div
                key={item.id}
                className="cart-item-row"
                data-testid={`cart-item-${item.id}`}
              >
                <div className="cart-item-details">
                  <span className="cart-item-name" data-testid={`cart-item-name-${item.id}`}>
                    {item.name}
                  </span>
                  <span className="cart-item-unit-price">
                    ${item.price.toFixed(2)} each
                  </span>
                </div>

                <div className="cart-item-actions">
                  <div className="mini-qty-controls">
                    <button
                      type="button"
                      className="btn-mini-qty"
                      onClick={() => onDecrement(item.id)}
                      data-testid={`cart-decrement-btn-${item.id}`}
                      aria-label="Decrease quantity"
                    >
                      –
                    </button>
                    <span
                      className="mini-qty-value"
                      data-testid={`cart-item-qty-${item.id}`}
                    >
                      {item.quantity}
                    </span>
                    <button
                      type="button"
                      className="btn-mini-qty"
                      onClick={() => onIncrement(item.id)}
                      data-testid={`cart-increment-btn-${item.id}`}
                      aria-label="Increase quantity"
                    >
                      +
                    </button>
                  </div>

                  <span
                    className="cart-item-subtotal"
                    data-testid={`cart-item-subtotal-${item.id}`}
                  >
                    ${itemTotal.toFixed(2)}
                  </span>

                  <button
                    type="button"
                    className="btn-remove"
                    onClick={() => onRemove(item.id)}
                    data-testid={`cart-remove-btn-${item.id}`}
                    title="Remove item"
                    aria-label={`Remove ${item.name} from cart`}
                  >
                    ✕
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <div className="cart-divider" />

      <div className="cart-totals">
        <div className="total-row subtotal">
          <span>Subtotal</span>
          <span data-testid="cart-subtotal-amount">${orderTotal.toFixed(2)}</span>
        </div>
        <div className="total-row taxes">
          <span>Taxes & Fees</span>
          <span className="free-badge">Included ($0.00)</span>
        </div>
        <div className="total-row final-total">
          <span className="total-label">Final Order Total</span>
          <span className="total-value" data-testid="order-total-amount">
            ${orderTotal.toFixed(2)}
          </span>
        </div>
      </div>
    </div>
  );
}
