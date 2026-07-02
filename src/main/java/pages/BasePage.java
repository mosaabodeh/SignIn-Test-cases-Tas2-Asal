package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.web.locators.ElementKey;
import pages.web.locators.ElementRegistry;
import utils.ConfigReader;

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
     public static String verifyName(String name) {
        String xpathExpression = String.format("//android.widget.EditText[@text='%s']", name);
        return driver.findElement(io.appium.java_client.AppiumBy.xpath(xpathExpression)).getText(); }

    protected By locator(ElementKey key) {
        String platform = driver.getClass().getSimpleName().contains("Android")
                ? "android"
                : "web";

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
    protected void waitForButtons(By buttonContainer, By buttonText, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        WebElement containerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(buttonContainer));

        wait.until(driverInstance -> {
            WebElement element = driverInstance.findElement(buttonContainer);
            String ariaDisabled = element.getAttribute("aria-disabled");
            return "false".equals(ariaDisabled) || ariaDisabled == null;
        });
        wait.until(ExpectedConditions.elementToBeClickable(buttonContainer));
        try {
            containerElement.click();
            System.out.println("✅ Primary action button container click complete.");
        } catch (Exception e) {
            System.out.println("⚠️ Interaction intercepted by layout overlay. Falling back to JavaScript engine click wrapper...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", containerElement);
        }
    }

    public WebElement waitForElement(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


}