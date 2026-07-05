package pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;
import pages.base.BaseDashboard;
import pages.locators.ElementKey;

import java.time.Duration;

public class DashboardWebPage extends BasePage   implements BaseDashboard {

    public DashboardWebPage(WebDriver driver) {
        super(driver);
    }

    public void dismissPopupIfPresent() {
    try {
        WebElement popupCloseButton = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".modal-close, .popup-close, [aria-label='Close'], .cdk-overlay-backdrop")));
        popupCloseButton.click();
    } catch (TimeoutException e) {
    }
}
    @Override
    public boolean verifyUserNameThatLoggedIn(String fullName) {
        dismissPopupIfPresent();
        System.out.println("Opening profile menu...");
        click(locator(ElementKey.NAVIGATION_SETTINGS));

        System.out.println("Opening My Profile...");
        click(locator(ElementKey.MY_PROFILE_BUTTON));

        WebElement nameElement = waitForElement(locator(ElementKey.PROFILE_NAME), 15);

        String actualName = nameElement.getAttribute("textContent");

        if (actualName == null || actualName.isBlank()) {
            actualName = nameElement.getText();
        }

        actualName = actualName.trim();

        System.out.println("Logged in user: " + actualName);

        click(locator(ElementKey.CLOSE_BUTTON));

        String expectedName = (fullName + " " ).trim();

        return actualName.equalsIgnoreCase(expectedName);
    }
}
