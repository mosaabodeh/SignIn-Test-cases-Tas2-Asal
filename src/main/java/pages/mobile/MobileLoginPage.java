package pages.mobile;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.base.BaseLoginPage;

import pages.locators.ElementKey;

import static drivers.DriverFactory.getDriver;

public class MobileLoginPage extends BasePage implements BaseLoginPage  {

    public MobileLoginPage(WebDriver driver) {
        super(driver);
    }
    @Override
    public void enterUsername(String username) {
        type(locator(ElementKey.EMAIL_FIELD), username);
        hideKeyboardIfVisible();
           }

    @Override
    public void enterPassword(String password) {
        type(locator(ElementKey.PASSWORD_FIELD), password);
        hideKeyboardIfVisible();
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