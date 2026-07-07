package pages.web;

import org.openqa.selenium.*;

import pages.BasePage;
import pages.base.BaseLoginPage;
import pages.locators.ElementKey;


public class WebLoginPage extends BasePage implements BaseLoginPage {

    private final WebDriver driver;

    public WebLoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    @Override
    public void enterUsername(String user) {
        WebElement email = waitForElement(locator(ElementKey.EMAIL_FIELD), 4);
        email.click();
        email.sendKeys(user);
    }

    @Override
    public void enterPassword(String pass) {
        WebElement password = waitForElement(locator(ElementKey.PASSWORD_FIELD), 4);
        password.click();
        password.sendKeys(pass);
    }

    @Override
    public void clickContinue() {
        org.openqa.selenium.By continueLocator = locator(ElementKey.CONTINUE_BUTTON);
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                driver.findElement(continueLocator).click();

                org.openqa.selenium.support.ui.WebDriverWait miniWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofMillis(250));
                miniWait.until(org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf(driver.findElement(continueLocator)));

                System.out.println("🎯 Navigation Success on attempt: " + (i + 1));
                return;
            } catch (Exception e) {
                System.out.println("🔄 Lost click registered, retrying immediate native action... Attempt: " + (i + 2));
            }
        }
    }

    @Override
    public void clickLogin() {
        org.openqa.selenium.By loginLocator = locator(ElementKey.LOGIN_BUTTON);
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                driver.findElement(loginLocator).click();

                org.openqa.selenium.support.ui.WebDriverWait miniWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofMillis(250));
                miniWait.until(org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf(driver.findElement(loginLocator)));

                System.out.println("🎯 Login Success and page navigated on attempt: " + (i + 1));
                return;
            } catch (Exception e) {
                System.out.println("🔄 Lost login click registered, retrying native click... Attempt: " + (i + 2));
            }
        }
    }

    @Override
    public String getErrorMessage() {
        return waitForElement(locator(ElementKey.ERROR_MESSAGE), 5)
                .getText()
                .trim();
    }

    public boolean isButtonNotClickable() {
        try {
            WebElement button = driver.findElement(locator(ElementKey.CONTINUE_BUTTON));

            String ariaDisabled = button.getAttribute("aria-disabled");

            if ("true".equalsIgnoreCase(ariaDisabled)) {
                return true;
            }

            return !button.isEnabled();

        } catch (Exception e) {
            System.out.println("Unable to verify button state: " + e.getMessage());
            return false;
        }
    }


    private void clickElementSafely(By locator) {
        WebElement element = waitForClickability(locator);

        try {
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", element);
        }
    }
}