package com.checkoutproof.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.File;
import java.time.Duration;

/**
 * DriverManager provides and manages Microsoft Edge WebDriver instances.
 * Explicitly configures the local msedgedriver.exe executable to avoid
 * network dependencies or download failures with Selenium Manager.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Resolves the local msedgedriver.exe executable path across multiple candidate locations.
     */
    public static File resolveEdgeDriverExecutable() {
        // 1. Check system property if explicitly provided via -Dwebdriver.edge.driver
        String systemProp = System.getProperty("webdriver.edge.driver");
        if (systemProp != null && !systemProp.trim().isEmpty()) {
            File f = new File(systemProp);
            if (f.exists() && f.isFile()) {
                return f.getAbsoluteFile();
            }
        }

        // 2. Check project drivers folder and standard known locations
        File[] candidatePaths = new File[] {
            new File("drivers/msedgedriver.exe"),
            new File("automation/drivers/msedgedriver.exe"),
            new File("C:/Users/bakya/.gemini/antigravity/scratch/checkoutproof-ai/automation/drivers/msedgedriver.exe"),
            new File("C:/Users/bakya/Downloads/edgedriver_win64/msedgedriver.exe"),
            new File("C:/Users/bakya/Downloads/msedgedriver.exe")
        };

        for (File candidate : candidatePaths) {
            if (candidate.exists() && candidate.isFile()) {
                return candidate.getAbsoluteFile();
            }
        }

        return null;
    }

    /**
     * Initializes a fresh Microsoft Edge browser instance with InPrivate browsing.
     */
    public static void initializeDriver() {
        File driverFile = resolveEdgeDriverExecutable();
        if (driverFile == null) {
            throw new IllegalStateException(
                "Could not locate msedgedriver.exe in automation/drivers/ or Downloads folder. " +
                "Please verify msedgedriver.exe exists."
            );
        }

        // Explicitly set system property
        System.setProperty("webdriver.edge.driver", driverFile.getAbsolutePath());

        // Build EdgeDriverService using the local executable
        EdgeDriverService service = new EdgeDriverService.Builder()
                .usingDriverExecutable(driverFile)
                .usingAnyFreePort()
                .build();

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--inprivate"); // Clean, independent session

        // Allow headless execution if specified via system property -Dheadless=true
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new EdgeDriver(service, options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // Explicit waits only
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        driverThreadLocal.set(driver);
    }

    /**
     * Retrieves the WebDriver instance associated with the current test thread.
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Safely closes the browser and cleans up ThreadLocal resources.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("[DriverManager] Warning during browser quit: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
