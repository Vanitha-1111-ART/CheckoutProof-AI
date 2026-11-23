package com.checkoutproof.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model representing the CheckoutProof AI React Checkout Page.
 * Uses exact data-testid selectors defined in the frontend components.
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // --- Product Catalog Locators (Actual data-testid from ProductList.jsx) ---
    private final By product1AddBtn = By.cssSelector("[data-testid='add-to-cart-btn-1']");
    private final By product2AddBtn = By.cssSelector("[data-testid='add-to-cart-btn-2']");
    private final By product1IncrementBtn = By.cssSelector("[data-testid='increment-btn-1']");
    private final By product2IncrementBtn = By.cssSelector("[data-testid='increment-btn-2']");
    private final By product1Quantity = By.cssSelector("[data-testid='product-quantity-1']");
    private final By product2Quantity = By.cssSelector("[data-testid='product-quantity-2']");

    // --- Cart Summary Locators (Actual data-testid from CartSummary.jsx) ---
    private final By cartEmptyMessage = By.cssSelector("[data-testid='cart-empty-message']");
    private final By product1Subtotal = By.cssSelector("[data-testid='cart-item-subtotal-1']");
    private final By product2Subtotal = By.cssSelector("[data-testid='cart-item-subtotal-2']");
    private final By orderTotal = By.cssSelector("[data-testid='order-total-amount']");

    // --- Checkout Form Locators (Actual data-testid from CheckoutForm.jsx) ---
    private final By customerNameInput = By.cssSelector("[data-testid='customer-name-input']");
    private final By placeOrderBtn = By.cssSelector("[data-testid='place-order-btn']");
    private final By errorBanner = By.cssSelector("[data-testid='error-message-banner']");
    private final By errorText = By.cssSelector("[data-testid='error-message-text']");

    // --- Order Confirmation Locators (Actual data-testid from OrderConfirmation.jsx) ---
    private final By confirmationScreen = By.cssSelector("[data-testid='order-confirmation-screen']");
    private final By confirmationCustomerName = By.cssSelector("[data-testid='order-customer-name']");
    private final By confirmationRefNumber = By.cssSelector("[data-testid='order-reference-number']");
    private final By confirmationTotal = By.cssSelector("[data-testid='order-final-total']");
    private final By startNewOrderBtn = By.cssSelector("[data-testid='start-new-order-btn']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='checkoutproof-app']")));
    }

    // --- Actions ---

    public void addProduct1ToCart() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(product1AddBtn));
        btn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(product1IncrementBtn));
    }

    public void addProduct2ToCart() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(product2AddBtn));
        btn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(product2IncrementBtn));
    }

    public void incrementProduct1() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(product1IncrementBtn));
        btn.click();
    }

    public void enterCustomerName(String name) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(customerNameInput));
        input.clear();
        input.sendKeys(name);
    }

    public void clickPlaceOrder() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn));
        btn.click();
    }

    // --- Verifications & Getters ---

    public boolean isCartEmptyMessageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartEmptyMessage)).isDisplayed();
    }

    public String getProduct1Subtotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(product1Subtotal)).getText().trim();
    }

    public String getProduct2Subtotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(product2Subtotal)).getText().trim();
    }

    public String getOrderTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderTotal)).getText().trim();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorBanner));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorText)).getText().trim();
    }

    public boolean waitForConfirmationScreen() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationScreen)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isConfirmationScreenDisplayed() {
        try {
            return !driver.findElements(confirmationScreen).isEmpty() &&
                   driver.findElement(confirmationScreen).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getConfirmedCustomerName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationCustomerName)).getText().trim();
    }

    public String getConfirmedReferenceNumber() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationRefNumber)).getText().trim();
    }

    public String getConfirmedTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationTotal)).getText().trim();
    }
}
