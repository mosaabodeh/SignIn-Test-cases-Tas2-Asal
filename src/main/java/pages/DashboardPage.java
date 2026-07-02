package pages;

import org.openqa.selenium.WebDriver;
import pages.locators.ElementKey;


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

        return firstName.equalsIgnoreCase(realFirstName)&& lastName.equalsIgnoreCase(realLastName);
    }
    public boolean isDashboardDisplayed() {
        return isDisplayed(locator(ElementKey.NEWS_DASHBOARD));
    }
}
