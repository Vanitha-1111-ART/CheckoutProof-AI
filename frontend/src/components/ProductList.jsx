import React from 'react';

export default function ProductList({ products, cart, onAddToCart, onIncrement, onDecrement }) {
  return (
    <section className="products-section" aria-label="Available Products">
      <div className="section-header">
        <h2>Available Products</h2>
        <span className="section-subtitle">Select items to add to your checkout order</span>
      </div>

      <div className="products-grid">
        {products.map((product) => {
          const cartItem = cart.find((item) => item.id === product.id);
          const quantity = cartItem ? cartItem.quantity : 0;

          return (
            <div
              key={product.id}
              className={`product-card ${quantity > 0 ? 'in-cart' : ''}`}
              data-testid={`product-card-${product.id}`}
            >
              <div className="product-badge">{product.badge}</div>
              <div className="product-icon">{product.icon}</div>
              <h3 className="product-title" data-testid={`product-title-${product.id}`}>
                {product.name}
              </h3>
              <p className="product-desc">{product.description}</p>
              
              <div className="product-footer">
                <div className="product-price-box">
                  <span className="price-label">Price</span>
                  <span className="product-price" data-testid={`product-price-${product.id}`}>
                    ${product.price.toFixed(2)}
                  </span>
                </div>

                <div className="product-actions">
                  {quantity === 0 ? (
                    <button
                      type="button"
                      className="btn btn-secondary add-btn"
                      onClick={() => onAddToCart(product)}
                      data-testid={`add-to-cart-btn-${product.id}`}
                    >
                      + Add to Cart
                    </button>
                  ) : (
                    <div className="quantity-controls" data-testid={`quantity-controls-${product.id}`}>
                      <button
                        type="button"
                        className="btn-qty"
                        onClick={() => onDecrement(product.id)}
                        aria-label={`Decrease ${product.name} quantity`}
                        data-testid={`decrement-btn-${product.id}`}
                      >
                        –
                      </button>
                      <span
                        className="quantity-display"
                        data-testid={`product-quantity-${product.id}`}
                      >
                        {quantity}
                      </span>
                      <button
                        type="button"
                        className="btn-qty"
                        onClick={() => onIncrement(product.id)}
                        aria-label={`Increase ${product.name} quantity`}
                        data-testid={`increment-btn-${product.id}`}
                      >
                        +
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}
