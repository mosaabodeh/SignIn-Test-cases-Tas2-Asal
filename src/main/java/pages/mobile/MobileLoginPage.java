package pages.mobile;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.base.BaseLoginPage;
import pages.web.locators.ElementKey;

public class MobileLoginPage extends BasePage implements BaseLoginPage {

    public MobileLoginPage(WebDriver driver) {
        super(driver);
    }
    @Override
    public void enterUsername(String username) {
        type(locator(ElementKey.EMAIL_FIELD), username);
    }

    @Override
    public void enterPassword(String password) {
        type(locator(ElementKey.PASSWORD_FIELD), password);
    }

    @Override
    public void clickContinue() {
        click(locator(ElementKey.CONTINUE_BUTTON));
    }

    @Override
    public void clickLogin() {
        click(locator(ElementKey.LOGIN_BUTTON));
    }

    @Override
    public boolean isDashboardDisplayed() {
        return isDisplayed(locator(ElementKey.NEWS_DASHBOARD));
    }

    public void logOut() {
        click(locator(ElementKey.NAVIGATION_BACK));
        click(locator(ElementKey.NAVIGATION_SETTINGS));
        click(locator(ElementKey.LOGOUT_BUTTON));
        click(locator(ElementKey.LOGOUT_CONFIRM));
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

    @Override
    public String getErrorMessage() {

        try {
            if (driver instanceof AndroidDriver mobileDriver) {
                if (mobileDriver.isKeyboardShown()) {
                    mobileDriver.hideKeyboard();
                }
                String toastText =utils.ToastOcrHandler.captureAndReadToast(mobileDriver);
                return toastText == null ? "": toastText.replaceAll("\\s+", " ").trim();
            }
            return "";

        } catch (Exception e) {

            System.out.println("⚠️ Mobile OCR error: " + e.getMessage());
            return "";

        }
    }
}