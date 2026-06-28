package pages.mobile;

import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.ElementsPage;
import pages.base.BaseLoginPage;

public class MobileLoginPage extends BasePage implements BaseLoginPage  {

    public MobileLoginPage(WebDriver driver) {
        super( driver);
        this.driver = driver;
    }

    @Override
    public void enterUsername(String username) {
        org.openqa.selenium.WebElement emailField = waitForVisibility(ElementsPage.EMAIL_FIELD);

        emailField.clear();

        emailField.sendKeys(username);
    }

    @Override
    public void enterPassword(String password) {
        waitForVisibility( ElementsPage.PASSWORD_FIELD).sendKeys(password);
    }
    @Override
    public void clickContinue() {
        waitForClickability( ElementsPage.CONTINUE_BUTTON).click();
    }

    @Override
    public void logOut(){
        waitForClickability(ElementsPage.NAVIGATION_BACK).click();
        waitForClickability(ElementsPage.NAVIGATION_SETTINGS).click();
        waitForClickability(ElementsPage.LOGOUT_BUTTON).click();
        waitForClickability(ElementsPage.LOGOUT_CONFIRM).click();

    }
    @Override
    public boolean verifyUserNameThatLoggedIn(String firstName, String lastName) {
        waitForClickability(ElementsPage.NAVIGATION_SETTINGS).click();
        waitForClickability(ElementsPage.MY_PROFILE_BUTTON).click();

        String realFirstName= ElementsPage.verifyName(firstName);
        String realLastName= ElementsPage.verifyName(lastName);
        System.out.println("The User Firest name is : "+realFirstName+ "   And the last name is : "+realLastName);

        return firstName.equalsIgnoreCase(realFirstName) && (lastName.equalsIgnoreCase(realLastName));
    }

    @Override
    public void clickLogin() {
        waitForClickability( ElementsPage.LOGIN_BUTTON).click();
    }

    @Override
    public boolean isDashboardDisplayed() {
        return waitForVisibility( ElementsPage.NEWS).isDisplayed();
    }

    @Override
    public String getErrorMessage() {
        System.out.println("📱 Platform is Mobile. Dismissing keyboard and triggering OCR Toast Scraper...");
        try {
            io.appium.java_client.android.AndroidDriver mobileDriver = (io.appium.java_client.android.AndroidDriver) driver;
            if (mobileDriver.isKeyboardShown()) {
                mobileDriver.hideKeyboard();
            }
            String toastText = utils.ToastOcrHandler.captureAndReadToast(mobileDriver);
            return (toastText != null) ? toastText.replaceAll("\\s+", " ").trim() : "";
        } catch (Exception e) {
            System.out.println("⚠️ Mobile view handling or OCR issue: " + e.getMessage());
            return "";
        }
    }
}