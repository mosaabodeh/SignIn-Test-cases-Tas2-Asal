package pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver; // Switch from AndroidDriver to generic WebDriver
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import utils.ToastOcrHandler;

import java.time.Duration;

public class BasePage {
    protected static WebDriver driver;
    protected static WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        String timeoutProp = ConfigReader.getProperty("timeout.explicit");
        long defaultTimeout = (timeoutProp != null) ? Long.parseLong(timeoutProp) : 10;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    }

    protected  WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    protected static WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }


    protected void sendKeys(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.click();
        element.clear();
        element.sendKeys(text);
    }
    public String toastMessage() {
        String parsedText = "";
        try {
            parsedText = ToastOcrHandler.captureAndReadToast((io.appium.java_client.AppiumDriver) driver);
            if (parsedText == null || parsedText.trim().isEmpty()) {
                System.out.println("⚠️ OCR Scraper returned empty layout text.");
                return "";
            }

            parsedText = parsedText.replaceAll("\\s+", " ").trim();
            System.out.println("📸 [OCR Extracted Viewport Context]: " + parsedText);

        } catch (Exception e) {
            System.out.println("⚠️ OCR Scanner engine runtime mismatch: " + e.getMessage());
        }
        return parsedText;
    }

}