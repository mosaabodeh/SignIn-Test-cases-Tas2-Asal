package pages;

import constanse.Platforms;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.locators.ElementKey;
import pages.locators.ElementRegistry;
import utils.ConfigReader;

import java.time.Duration;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private final String platform= Platforms.CurrentPlatform;

    public BasePage(WebDriver driver) {
        this.driver = driver;

        String timeoutProp = ConfigReader.getProperty("timeout.explicit");
        long defaultTimeout = (timeoutProp != null)
                ? Long.parseLong(timeoutProp)
                : 10;

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    }

    protected By locator(ElementKey key) {
        return ElementRegistry.getLocator(key, platform);
    }

    protected void click(By locator) {
        waitForClickability(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.click();
        element.clear();
        element.sendKeys(text);
    }
    public void hideKeyboardIfVisible() {
        try {
            if (driver instanceof io.appium.java_client.HidesKeyboard) {
                ((io.appium.java_client.HidesKeyboard) driver).hideKeyboard();
            }
        } catch (Exception e) {
        }
    }
    public void waitForStable(org.openqa.selenium.By locator) {
        int maxRetries = 3;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                org.openqa.selenium.WebElement element = driver.findElement(locator);
                element.click();

                org.openqa.selenium.support.ui.WebDriverWait miniWait =
                        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofMillis(250));
                miniWait.until(org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf(element));

                System.out.println("🎯 Navigation Success on attempt: " + (i + 1));
                return;
            } catch (Exception e) {
                lastException = e;
                System.out.println("🔄 Lost click or element unstable, retrying... Attempt " + (i + 1) + " of " + maxRetries);
            }
        }
        System.out.println("❌ All " + maxRetries + " attempts exhausted. Failing the action explicitly.");
        throw new org.openqa.selenium.WebDriverException(
                "❌ [Unstable UI Failure] Failed to interact stably with element after " + maxRetries + " attempts.",
                lastException
        );
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForElement(By locator, int timeoutInSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForButtons(By buttonContainer , int timeoutInSeconds) {

        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));

        WebElement container = customWait.until(
                ExpectedConditions.visibilityOfElementLocated(buttonContainer)
        );

        customWait.until(d -> {
            WebElement el = d.findElement(buttonContainer);
            String ariaDisabled = el.getAttribute("aria-disabled");
            return ariaDisabled == null || "false".equals(ariaDisabled);
        });

        try {
            customWait.until(ExpectedConditions.elementToBeClickable(buttonContainer));
            container.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", container);
        }
    }

}