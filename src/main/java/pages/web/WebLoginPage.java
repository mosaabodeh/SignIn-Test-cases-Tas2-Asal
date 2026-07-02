package pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;
import pages.ElementsPage;
import pages.base.BaseLoginPage;
import java.time.Duration;

public class WebLoginPage extends BasePage implements BaseLoginPage {
    private final WebDriver driver;

    public WebLoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    @Override
    public void enterUsername(String user) {
        WebElement email = waitForElement(ElementsPage.usernameField, 4);
        email.click();
        email.sendKeys(user);
    }

    @Override
    public void enterPassword(String pass) {
        WebElement password = waitForElement(ElementsPage.passwordField, 4);
        password.click();
        password.sendKeys(pass);
    }

    @Override
    public void clickLogin() {
        waitForButtons(ElementsPage.loginButtonContainer, ElementsPage.loginButtonText, 3);
    }

    @Override
    public boolean isDashboardDisplayed() {
        try {
            WebElement element = waitForElement(ElementsPage.newsFeedButton, 20);
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            System.out.println("❌ Channels element is either missing, hidden, or disabled.");
            return false;
        }
    }
    @Override
    public String getErrorMessage() {
        org.openqa.selenium.WebElement errorElement = waitForElement(ElementsPage.errorBannerMessage,3);
        return errorElement.getText().trim();
    }

    @Override
    public void clickContinue() {
        waitForButtons(ElementsPage.continueButtonContainer, ElementsPage.continueButtonText, 5);
    }

    public boolean isButtonNotClickable() {
        try {
            WebElement buttonContainer = driver.findElement(ElementsPage.continueButtonContainer);

            String ariaDisabled = buttonContainer.getAttribute("aria-disabled");
            System.out.println("ℹ️ Verification State Log - 'aria-disabled' is: " + ariaDisabled);

            if ("true".equalsIgnoreCase(ariaDisabled)) {
                System.out.println("✅ Confirmed: Button is verified unclickable via framework state configuration attributes.");
                return true;
            }

            if (!buttonContainer.isEnabled()) {
                System.out.println("✅ Confirmed: Button is verified unclickable via native browser properties.");
                return true;
            }

            System.out.println("⚠️ Alert: The button appears active and is structurally clickable.");
            return false;

        } catch (Exception e) {
            System.out.println("❌ Element not found or unreachable in DOM tree: " + e.getMessage());
            return false;
        }
    }
    public boolean verifyUserNameThatLoggedIn(String firstName, String lastName) {

        System.out.println("⏳ Layer Sync [1/3]: Clicking profile avatar menu custom element...");
        waitForButtons(ElementsPage.profileMenuAvatar, ElementsPage.innerAvatarLabel, 15);

        System.out.println("⏳ Layer Sync [2/3]: Clicking 'My Profile' dropdown option item...");
        waitForButtons(ElementsPage.myProfileContainer, ElementsPage.myProfileText, 5);

        System.out.println("⏳ Layer Sync [3/3]: Waiting for profile name display container to settle...");
        waitForElement( ElementsPage.profileMenuUsernameLabel, 5);

        WebElement nameElement = driver.findElement(ElementsPage.profileMenuUsernameLabel);
        String realFullName = nameElement.getAttribute("textContent");

        if (realFullName == null || realFullName.trim().isEmpty()) {
            realFullName = nameElement.getText();
        }

        realFullName = realFullName.trim();
        System.out.println("👤 [UI Verification] Logged in profile identifier from DOM: " + realFullName);

        String expectedFullName = (firstName + " " + lastName).trim();
        clickElementSafely(ElementsPage.closeIconButton, 5);

        return realFullName.equalsIgnoreCase(expectedFullName);
    }


public void logOut() {
    clickElementSafely(ElementsPage.profileMenuAvatar, 5);
    System.out.println("🎯 LogOut Flow: Avatar container element clicked.");

    System.out.println("⏳ Layer Sync [1/2]: Clicking 'Log out' dropdown option item...");
    waitForButtons(ElementsPage.logoutContainer, ElementsPage.logoutTextLabel, 5);

    System.out.println("⏳ Layer Sync [2/2]: Clicking confirmation modal 'Log out' action button...");
    waitForButtons(ElementsPage.logoutConfirmContainer, ElementsPage.logoutConfirmTextLabel, 5);

    System.out.println("🚪 Logged out cleanly from the Web workspace dashboard environment view.");
}
    private void clickElementSafely(By locator, int timeoutInSeconds) {
        WebElement targetElement = waitForElement(locator, timeoutInSeconds);
        try {
            targetElement.click();
        } catch (Exception e) {
            System.out.println("⚡ Layout animation element blocked standard tap on " + locator.toString() + ". Invoking JS injector bypass...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", targetElement);
        }
    }
}