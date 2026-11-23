package com.checkoutproof.base;

import com.checkoutproof.pages.CheckoutPage;
import com.checkoutproof.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest provides lifecycle management for all test classes.
 * Ensures every single test runs with a fresh, isolated Edge browser session
 * and a completely clean cart state.
 */
public abstract class BaseTest {

    protected static final String APP_URL = "http://127.0.0.1:5173";
    protected WebDriver driver;
    protected CheckoutPage checkoutPage;

    @BeforeMethod
    public void setUp() {
        // Starts fresh browser instance in private mode for zero state leakage
        DriverManager.initializeDriver();
        driver = DriverManager.getDriver();
        checkoutPage = new CheckoutPage(driver);
        checkoutPage.open(APP_URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Always closes the browser after every test run
        DriverManager.quitDriver();
    }
}
