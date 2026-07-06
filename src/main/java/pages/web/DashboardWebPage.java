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
            By popupButtonLocator = By.xpath("//button[contains(., 'Allow') or contains(., 'Close') or @aria-label='Close']");

            WebElement targetButton = new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.elementToBeClickable(popupButtonLocator));

            try {
                targetButton.click();
                System.out.println("✅ [Web Banner] Audio permission popup dismissed normally via Click.");
            } catch (Exception e) {
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", targetButton);
                System.out.println("⚡ [Web Banner] Audio permission popup forced to dismiss via JS Click.");
            }
        } catch (TimeoutException e) {
            System.out.println("ℹ️ No audio connectivity banner appeared, skipping bypass loop.");
        }
    }
    @Override
    public boolean verifyUserNameThatLoggedIn(String fullName) {
        dismissPopupIfPresent();
        System.out.println("Opening profile menu...");
        click(locator(ElementKey.USER_MENU));
        dismissPopupIfPresent();
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

        String expectedName = fullName.trim();

        return actualName.equalsIgnoreCase(expectedName);
    }
}
