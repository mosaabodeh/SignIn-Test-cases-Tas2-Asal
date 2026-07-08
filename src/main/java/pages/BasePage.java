package pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.locators.ElementKey;
import pages.locators.ElementRegistry;

import java.time.Duration;

public class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final String platform;

    public BasePage(WebDriver driver, String platform) {
        this.driver = driver;
        this.platform = platform.toLowerCase().trim();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
    public void waitForStable(org.openqa.selenium.By locator){
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                driver.findElement(locator).click();

                org.openqa.selenium.support.ui.WebDriverWait miniWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofMillis(250));
                miniWait.until(org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf(driver.findElement(locator)));

                System.out.println("🎯 Navigation Success on attempt: " + (i + 1));
                return;
            } catch (Exception e) {
                System.out.println("🔄 Lost click registered, retrying immediate native action... Attempt: " + (i + 2));
            }
        }
        System.out.println("fail for this time (:)");
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

    public String verifyName(String name) {
        String xpath = String.format("//android.widget.EditText[@text='%s']", name);
        return driver.findElement(AppiumBy.xpath(xpath)).getText();
    }
}