package pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.BasePage;
import pages.base.BaseDashboard;
import pages.locators.ElementKey;

import static pages.locators.ElementRegistry.getNameFieldLocator;

public class DashboardAndroidPage extends BasePage implements BaseDashboard {

    public DashboardAndroidPage(WebDriver driver) {
        super(driver);
    }
@Override
    public boolean verifyUserNameThatLoggedIn(String fullName) {
        String[] nameParts = fullName.trim().split("\\s+");
        String firstName = nameParts[0];
        String lastName = (nameParts.length > 1) ? nameParts[nameParts.length - 1] : "";

        click(locator(ElementKey.USER_MENU));
        click(locator(ElementKey.MY_PROFILE_BUTTON));

        System.out.println("Expected Input -> First Name: " + firstName + " | Last Name: " + lastName);

        String realFirstName = getNameFieldLocator(firstName);
        String realLastName = getNameFieldLocator(lastName);

        System.out.println("Actual Found -> First Name: " + realFirstName + " | Last Name: " + realLastName);

        hideKeyboardIfVisible();
        click(locator(ElementKey.NAVIGATION_BACK));

        String expectedFirst = firstName.contains(" ") ? firstName.split("\\s+")[0] : firstName;

        return expectedFirst.equalsIgnoreCase(realFirstName) && lastName.equalsIgnoreCase(realLastName);
    }

    @Override
    public boolean verifyUserName(String fullName) {
        click(locator(ElementKey.USER_MENU));

        String realName= wait.until(ExpectedConditions.visibilityOfElementLocated( locator(ElementKey.USER_FULL_NAME))).getText().trim();
        return realName.equalsIgnoreCase(fullName);
    }

}
