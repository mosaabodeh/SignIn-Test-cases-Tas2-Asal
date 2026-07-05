package pages.web;

import org.openqa.selenium.*;

import pages.BasePage;
import pages.base.BaseLoginPage;
import pages.locators.ElementKey;


public class WebLoginPage extends BasePage implements BaseLoginPage {

    private static final String PLATFORM = "web";
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
       waitForVisibility(locator(ElementKey.CONTINUE_BUTTON));
       clickElementSafely(locator(ElementKey.CONTINUE_BUTTON));
    }

    @Override
    public void clickLogin() {
        clickElementSafely(locator(ElementKey.LOGIN_BUTTON));
    }

    @Override
    public String getErrorMessage() {
        return waitForElement(locator(ElementKey.ERROR_MESSAGE), 3)
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