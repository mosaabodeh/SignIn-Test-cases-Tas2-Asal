package pages;

import org.openqa.selenium.WebDriver;
import pages.base.BaseLogoutPage;
import pages.locators.ElementKey;

public class LogoutPage extends BasePage implements BaseLogoutPage {

    public LogoutPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void logOut() {

        click(locator(ElementKey.USER_MENU));
        click(locator(ElementKey.LOGOUT_BUTTON));
        click(locator(ElementKey.LOGOUT_CONFIRM));
    }
}