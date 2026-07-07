package pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.BasePage;
import pages.base.BaseDashboard;
import pages.locators.ElementKey;


public class DashboardAndroidPage extends BasePage implements BaseDashboard {

    public DashboardAndroidPage(WebDriver driver) {
        super(driver);
    }



    @Override
    public boolean verifyUserName(String fullName) {
        click(locator(ElementKey.USER_MENU));

        String realName= wait.until(ExpectedConditions.visibilityOfElementLocated( locator(ElementKey.USER_FULL_NAME))).getText().trim();
        return realName.equalsIgnoreCase(fullName);
    }

}
