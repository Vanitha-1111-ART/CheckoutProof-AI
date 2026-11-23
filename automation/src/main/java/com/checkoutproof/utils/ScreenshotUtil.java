package com.checkoutproof.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil captures browser screenshots upon test failure for visual verification
 * and future AI triage analysis.
 */
public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "reports/screenshots";

    /**
     * Captures a full browser screenshot and stores it in the reports/screenshots directory.
     *
     * @param driver   Active WebDriver instance
     * @param testName Name of the failed test
     * @return Path to the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            System.err.println("[ScreenshotUtil] WebDriver is null; cannot take screenshot.");
            return null;
        }

        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            Path destinationDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS"));
            String fileName = testName + "_" + timestamp + ".png";
            Path destinationPath = destinationDir.resolve(fileName);

            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[ScreenshotUtil] Failure screenshot captured: " + destinationPath.toAbsolutePath());
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("[ScreenshotUtil] Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }
}
