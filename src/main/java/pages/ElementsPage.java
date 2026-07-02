package pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ElementsPage  {

    // ==========================================
    // Mobile Android Elements
    // ==========================================
    public static final By EMAIL_FIELD = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)");

    public static final By PASSWORD_FIELD = AppiumBy.xpath("//android.widget.EditText[.//android.widget.TextView[@text='Password'] or @text='Password']");

    public static final By CONTINUE_BUTTON = AppiumBy.androidUIAutomator("new UiSelector().text(\"Continue\")");

    public static final By LOGIN_BUTTON = AppiumBy.androidUIAutomator("new UiSelector().text(\"Sign in\")");

    public static final By NEWS = AppiumBy.androidUIAutomator("new UiSelector().description(\"News you follow\")");

    public static final By NAVIGATION_SETTINGS = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@resource-id=\"com.ale.rainbow:id/compose_view\"]/android.view.View/android.view.View[1]");

    public static final By MY_PROFILE_BUTTON = AppiumBy.id("com.ale.rainbow:id/drawer_photo");

    public static final By NAVIGATION_BACK = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(28)");

    public static final By LOGOUT_BUTTON = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.ale.rainbow:id/drawer_exit\")");

    public static final By LOGOUT_CONFIRM = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"android:id/button1\")");
  /*  public static String verifyName(String name) {
        String xpathExpression = String.format("//android.widget.EditText[@text='%s']", name);
        return driver.findElement(io.appium.java_client.AppiumBy.xpath(xpathExpression)).getText();
    }*/

    // =========================================================================
// Web Desktop Chrome Locators (Completely Class-Agnostic)
// =========================================================================
    public static final By usernameField = By.xpath("//input[@type='email' or @id='username' or contains(@autocomplete,'username')]");
    public static final By passwordField = By.xpath("//input[@type='password' or @id='authPwd' or contains(@autocomplete,'current-password')]");

    public static final By continueButtonContainer = By.xpath("//button[contains(@class, 'c-button') and contains(., 'Continue')]");

    public static final By continueButtonText = By.xpath("//button[contains(@class, 'c-button')]//span[contains(@class, '__label')]");
    public static final By loginButtonContainer = By.xpath("//button[@type='submit' or contains(.,'Connect') or contains(.,'CONNECT') or contains(.,'Sign') or contains(.,'SIGN')]");
    public static final By loginButtonText = By.xpath("//button[@type='submit' or contains(.,'Connect') or contains(.,'CONNECT') or contains(.,'Sign') or contains(.,'SIGN')]//span");

    public static final By errorBannerMessage = By.xpath("//*[contains(@class, 'error') or contains(@class, 'Error') or @role='alert']");
    public static final By newsFeedButton = By.cssSelector("button[data-tour-id='channels']");
    public static final By logoutContainer = By.xpath("//rb-dropdown-item[@icon='logout']");

    public static final By logoutTextLabel = By.xpath("//rb-dropdown-item[@icon='logout']//div[@class='dropdown-item-label' and text()='Log out']");
    public static final By profileMenuAvatar = By.xpath("//rb-dropdown-elem[@id='userArea']");
    public static final By profileMenuUsernameLabel = By.xpath("//rb-dropdown-elem[@id='userArea']//label[contains(@class, 'u-visually-hidden')]");  public static String verifyName(org.openqa.selenium.WebDriver driver, String name) {
        String xpathExpression = String.format("//android.widget.EditText[@text='%s'] or //span[text()='%s']", name, name);
        return driver.findElement(org.openqa.selenium.By.xpath(xpathExpression)).getText();
    }
    public static final By innerAvatarLabel = By.cssSelector("rb-user-avatar#userAvatarMenu label.u-visually-hidden");

    public static final By logoutConfirmContainer = By.xpath("//rb-button[@labelid='logout']");
    public static final By logoutConfirmTextLabel = By.xpath("//rb-button[@labelid='logout']//span[@class='c-button__label' and contains(text(),'Log out')]");
    public static final By closeIconButton = By.xpath("//button[@aria-label='Close']");
    public static final By myProfileContainer = By.xpath("//rb-dropdown-item[@icon='profil']");
    public static final By myProfileText = By.xpath("//rb-dropdown-item[@icon='profil']//div[@class='dropdown-item-label' and text()='My profile']");
}
