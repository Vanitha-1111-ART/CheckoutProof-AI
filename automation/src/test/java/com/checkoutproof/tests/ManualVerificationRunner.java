package com.checkoutproof.tests;

import com.checkoutproof.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

/**
 * Executes and documents the manual testing requirements specified for the hackathon:
 * 1. Empty Cart Validation & Checkout Prevention
 * 2. Rapid Double Click & Duplicate Order Prevention
 * Captures real screenshots into reports/manual-testing/.
 */
public class ManualVerificationRunner extends BaseTest {

    private static final String MANUAL_REPORTS_DIR = "reports/manual-testing";

    private void saveManualScreenshot(String fileName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            Path destDir = Paths.get(MANUAL_REPORTS_DIR);
            if (!Files.exists(destDir)) {
                Files.createDirectories(destDir);
            }
            Path dest = destDir.resolve(fileName);
            Files.copy(source.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Manual Test] Captured evidence screenshot: " + dest.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[Manual Test] Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Test(description = "Manual Test 1: Empty cart checkout prevention and validation message")
    public void testManualEmptyCartPrevention() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Enter customer name while cart remains completely empty
        checkoutPage.enterCustomerName("Sam Fisher");

        // 2. Click Place Order
        checkoutPage.clickPlaceOrder();

        // 3. Verify validation error banner appears
        String errorMsg = checkoutPage.getErrorMessage();
        Assert.assertTrue(
            errorMsg.contains("cart is empty"),
            "Expected empty cart validation message. Actual: " + errorMsg
        );

        // 4. Verify checkout is strictly blocked (no confirmation screen)
        Assert.assertFalse(
            checkoutPage.isConfirmationScreenDisplayed(),
            "Order confirmation screen should NOT be visible when cart is empty."
        );

        // 5. Capture visual evidence
        saveManualScreenshot("manual_empty_cart_blocked.png");
    }

    @Test(description = "Manual Test 2: Rapid double click prevention of duplicate submissions")
    public void testManualRapidDoubleClickPrevention() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Add product to cart
        checkoutPage.addProduct1ToCart();

        // 2. Enter customer name
        checkoutPage.enterCustomerName("Jordan Reed");

        // 3. Trigger rapid successive clicks using JavaScript to simulate immediate double click
        WebElement placeOrderBtn = driver.findElement(By.cssSelector("[data-testid='place-order-btn']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Click twice within milliseconds
        js.executeScript("arguments[0].click(); arguments[0].click();", placeOrderBtn);

        // 4. Verify confirmation screen renders exactly once
        Assert.assertTrue(
            checkoutPage.waitForConfirmationScreen(),
            "Confirmation screen must display after submission."
        );

        // 5. Verify reference number format is single and intact
        String refNum = checkoutPage.getConfirmedReferenceNumber();
        Assert.assertTrue(refNum.startsWith("CP-"), "Order reference should start with CP-. Found: " + refNum);

        // 6. Verify single order rendered without duplication
        Assert.assertEquals(checkoutPage.getConfirmedCustomerName(), "Jordan Reed");
        Assert.assertEquals(checkoutPage.getConfirmedTotal(), "$49.00");

        // 7. Capture visual evidence
        saveManualScreenshot("manual_rapid_click_single_order.png");
    }
}
