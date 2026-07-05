package pages;

import org.openqa.selenium.WebDriver;
import pages.base.BaseDashboard;
import pages.locators.ElementKey;

import static drivers.DriverFactory.getDriver;
import static pages.locators.ElementRegistry.getNameFieldLocator;


public class DashboardAndroidPage extends BasePage  implements BaseDashboard {


    public DashboardAndroidPage(WebDriver driver) {
        super(driver);
    }
@Override
    public boolean verifyUserNameThatLoggedIn(String fullName) {
        String[] nameParts = fullName.trim().split("\\s+");
        String firstName = nameParts[0];
        String lastName = (nameParts.length > 1) ? nameParts[nameParts.length - 1] : "";

        click(locator(ElementKey.NAVIGATION_SETTINGS));
        click(locator(ElementKey.MY_PROFILE_BUTTON));

        System.out.println("Expected Input -> First Name: " + firstName + " | Last Name: " + lastName);

        String realFirstName = getNameFieldLocator(firstName);
        String realLastName = getNameFieldLocator(lastName);

        System.out.println("Actual Found -> First Name: " + realFirstName + " | Last Name: " + realLastName);

        try {
            if (getDriver() instanceof io.appium.java_client.HidesKeyboard) {
                ((io.appium.java_client.HidesKeyboard) getDriver()).hideKeyboard();
                System.out.println("Keyboard hidden successfully.");
            }
        } catch (Exception e) {
            System.out.println("Keyboard was not visible or couldn't be hidden.");
        }

        click(locator(ElementKey.NAVIGATION_BACK));

        String expectedFirst = firstName.contains(" ") ? firstName.split("\\s+")[0] : firstName;
        click(locator(ElementKey.NAVIGATION_SETTINGS));

        return expectedFirst.equalsIgnoreCase(realFirstName) && lastName.equalsIgnoreCase(realLastName);
    }

}
