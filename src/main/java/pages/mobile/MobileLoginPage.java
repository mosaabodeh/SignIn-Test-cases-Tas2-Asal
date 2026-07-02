package pages.mobile;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.base.BaseLoginPage;
import pages.base.BaseLogoutPage;
import pages.locators.ElementKey;

public class MobileLoginPage extends BasePage implements BaseLoginPage , BaseLogoutPage {

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
    public void logOut() {
        click(locator(ElementKey.NAVIGATION_BACK));
        click(locator(ElementKey.NAVIGATION_SETTINGS));
        click(locator(ElementKey.LOGOUT_BUTTON));
        click(locator(ElementKey.LOGOUT_CONFIRM));
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