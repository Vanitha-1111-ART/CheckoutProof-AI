package com.checkoutproof.tests;

import com.checkoutproof.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Regression Test Suite for CheckoutProof AI.
 * Contains three independent browser automation test cases covering the core checkout flows.
 */
public class CheckoutTests extends BaseTest {

    /**
     * Test Case 1: Successful Checkout Flow
     * Verifies adding a product, entering customer details, placing the order,
     * and validating the confirmed order details with a unique reference number.
     */
    @Test(description = "Verify successful checkout flow with valid item and customer name")
    public void testSuccessfulCheckout() {
        // 1. Verify clean initial state
        Assert.assertTrue(
            checkoutPage.isCartEmptyMessageDisplayed(),
            "Pre-condition failed: Cart should be empty at the start of the test."
        );

        // 2. Add Product 1 (DevOps Cloud Pass - $49.00)
        checkoutPage.addProduct1ToCart();

        // 3. Enter valid customer name
        String customerName = "Alex Morgan";
        checkoutPage.enterCustomerName(customerName);

        // 4. Place order
        checkoutPage.clickPlaceOrder();

        // 5. Assert confirmation screen is displayed
        Assert.assertTrue(
            checkoutPage.waitForConfirmationScreen(),
            "Confirmation screen should be displayed after valid checkout."
        );

        // 6. Assert customer name matches
        Assert.assertEquals(
            checkoutPage.getConfirmedCustomerName(),
            customerName,
            "Confirmed customer name does not match input."
        );

        // 7. Assert unique reference number format (e.g. CP-123456)
        String refNumber = checkoutPage.getConfirmedReferenceNumber();
        Assert.assertTrue(
            refNumber.startsWith("CP-"),
            "Order reference number should start with 'CP-'. Found: " + refNumber
        );
        Assert.assertTrue(
            refNumber.length() >= 8,
            "Order reference number should be at least 8 characters. Found: " + refNumber
        );

        // 8. Assert confirmed final total matches
        Assert.assertEquals(
            checkoutPage.getConfirmedTotal(),
            "$49.00",
            "Confirmed order total should be $49.00."
        );
    }

    /**
     * Test Case 2: Blank Customer Name Validation
     * Verifies that placing an order without a customer name fails with a visible error
     * and does not allow the transaction to proceed.
     */
    @Test(description = "Verify checkout is prevented when customer name is left blank")
    public void testBlankCustomerNameValidation() {
        // 1. Add Product 1 to cart so cart is not empty
        checkoutPage.addProduct1ToCart();

        // 2. Leave customer name blank and attempt to checkout
        checkoutPage.enterCustomerName("");
        checkoutPage.clickPlaceOrder();

        // 3. Assert clear validation error message is shown
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(
            errorMessage.contains("Customer Name is required"),
            "Expected error banner mentioning Customer Name is required. Actual message: " + errorMessage
        );

        // 4. Assert user is NOT taken to confirmation screen
        Assert.assertFalse(
            checkoutPage.isConfirmationScreenDisplayed(),
            "Checkout should not proceed to confirmation screen when customer name is blank."
        );
    }

    /**
     * Test Case 3: Quantity Change and Exact Total Verification ($127.00)
     * Verifies product additions, quantity incrementing, individual line-item subtotals,
     * and exact recalculation of the order total.
     * Math:
     * - Product 1 (DevOps Cloud Pass): 2 x $49.00 = $98.00
     * - Product 2 (SDET Automation Toolkit): 1 x $29.00 = $29.00
     * Total = $98.00 + $29.00 = $127.00
     */
    @Test(description = "Verify product quantity increment and exact final total calculation ($127.00)")
    public void testQuantityChangeAndExactTotalVerification() {
        // 1. Add Product 1 ($49.00) and increment to quantity 2
        checkoutPage.addProduct1ToCart();
        checkoutPage.incrementProduct1();

        // 2. Add Product 2 ($29.00) with quantity 1
        checkoutPage.addProduct2ToCart();

        // 3. Verify individual line-item subtotals in cart summary
        String product1Subtotal = checkoutPage.getProduct1Subtotal();
        Assert.assertEquals(
            product1Subtotal,
            "$98.00",
            "Product 1 subtotal should be $98.00 (2 x $49.00)."
        );

        String product2Subtotal = checkoutPage.getProduct2Subtotal();
        Assert.assertEquals(
            product2Subtotal,
            "$29.00",
            "Product 2 subtotal should be $29.00 (1 x $29.00)."
        );

        // 4. Verify exact final order total
        String finalTotal = checkoutPage.getOrderTotal();
        Assert.assertEquals(
            finalTotal,
            "$127.00",
            "Final order total should be exactly $127.00."
        );
    }
}
