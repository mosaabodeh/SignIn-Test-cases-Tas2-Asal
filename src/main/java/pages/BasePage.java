package pages;

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
    private final String platform="android";

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

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- WAITS ----------------

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

    // ---------------- SPECIAL ACTION ----------------

    protected void waitForButtons(By buttonContainer, By buttonText, int timeoutInSeconds) {

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

    // ---------------- OPTIONAL MOBILE UTILITY ----------------

    public String verifyName(String name) {
        String xpath = String.format("//android.widget.EditText[@text='%s']", name);
        return driver.findElement(AppiumBy.xpath(xpath)).getText();
    }
}