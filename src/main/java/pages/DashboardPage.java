package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.locators.ElementKey;

import java.time.Duration;


public class DashboardPage extends BasePage {
    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean verifyUserNameThatLoggedIn(String firstName, String lastName) {

        click(locator(ElementKey.NAVIGATION_SETTINGS));
        click(locator(ElementKey.MY_PROFILE_BUTTON));

        String realFirstName = verifyName(firstName);
        String realLastName =  verifyName(lastName);

        System.out.println(
                "The User First name is : "+ realFirstName+ " And the last name is : "+ realLastName);
        click(locator(ElementKey.NAVIGATION_BACK));
        return firstName.equalsIgnoreCase(realFirstName)&& lastName.equalsIgnoreCase(realLastName);
    }
    public void dismissPopupIfPresent() {
        try {
            WebElement popupCloseButton = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".modal-close, .popup-close, [aria-label='Close'], .cdk-overlay-backdrop")));
            popupCloseButton.click();
        } catch (TimeoutException e) {
            // no popup appeared — safe to continue
        }
    }
    public boolean verifyUserNameThatLoggedIn(String firstName) {
        dismissPopupIfPresent();
        System.out.println("Opening profile menu...");
        click(locator(ElementKey.NAVIGATION_SETTINGS));

        System.out.println("Opening My Profile...");
        click(locator(ElementKey.MY_PROFILE_BUTTON));

        WebElement nameElement = waitForElement(locator(ElementKey.PROFILE_NAME), 5);

        String actualName = nameElement.getAttribute("textContent");

        if (actualName == null || actualName.isBlank()) {
            actualName = nameElement.getText();
        }

        actualName = actualName.trim();

        System.out.println("Logged in user: " + actualName);

        click(locator(ElementKey.CLOSE_BUTTON));

        String expectedName = (firstName + " " ).trim();

        return actualName.equalsIgnoreCase(expectedName);
    }
    public boolean isDashboardDisplayed() {
        return isDisplayed(locator(ElementKey.NEWS_DASHBOARD));
    }
}
