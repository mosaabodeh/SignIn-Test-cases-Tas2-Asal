package pages.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

import static drivers.DriverFactory.getDriver;

public final class ElementRegistry {

    private ElementRegistry(){}

    private static final Map<ElementKey,By> MOBILE_LOCATORS =
            new EnumMap<>(ElementKey.class);

    private static final Map<ElementKey,By> WEB =
            new EnumMap<>(ElementKey.class);

    static{

        // Mobile
        MOBILE_LOCATORS.put(
                ElementKey.EMAIL_FIELD,
                AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)")
        );

        MOBILE_LOCATORS.put(
                ElementKey.PASSWORD_FIELD,
                AppiumBy.xpath("//android.widget.EditText[.//android.widget.TextView[@text='Password'] or @text='Password']")
        );

        MOBILE_LOCATORS.put(
                ElementKey.CONTINUE_BUTTON,
                AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(9)")
        );

        MOBILE_LOCATORS.put(
                ElementKey.LOGIN_BUTTON,
                AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(10)")
        );

        MOBILE_LOCATORS.put(
                ElementKey.NEWS_DASHBOARD,
                AppiumBy.androidUIAutomator("new UiSelector().description(\"News you follow\")")
        );

        MOBILE_LOCATORS.put(
                ElementKey.USER_MENU,
                AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@resource-id='com.ale.rainbow:id/compose_view']/android.view.View/android.view.View[1]")
        );

        MOBILE_LOCATORS.put(
                ElementKey.MY_PROFILE_BUTTON,
                AppiumBy.id("com.ale.rainbow:id/drawer_photo")
        );


        MOBILE_LOCATORS.put(
                ElementKey.NAVIGATION_BACK,
                AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(28)")
        );

        MOBILE_LOCATORS.put(
                ElementKey.LOGOUT_BUTTON,
                AppiumBy.id("com.ale.rainbow:id/drawer_exit")
        );

        MOBILE_LOCATORS.put(
                ElementKey.LOGOUT_CONFIRM,
                AppiumBy.id("android:id/button1")
        );


        // Web

        WEB.put(ElementKey.EMAIL_FIELD,
                By.xpath("//input[@type='email' or @id='username' or contains(@autocomplete,'username')]"));

        WEB.put(ElementKey.PASSWORD_FIELD,
                By.xpath("//input[@type='password' or @id='authPwd' or contains(@autocomplete,'current-password')]"));

        WEB.put(ElementKey.CONTINUE_BUTTON,
                By.xpath("//button[contains(@class,'c-button--primary')][.//span[contains(@class,'c-button__label') and contains(normalize-space(.), 'Continue')]]"));
        WEB.put(ElementKey.LOGIN_BUTTON,
                By.xpath("//button[@type='submit' or contains(.,'Connect') or contains(.,'Sign')]"));
        WEB.put(
                ElementKey.ERROR_MESSAGE,
                By.xpath("//div[contains(@class, 'authWindowContent__inputErrorMessage')]/span")
        );

// Profile avatar
        WEB.put(
                ElementKey.PROFILE_MENU,
                By.cssSelector("app-profile-menu")
        );

// Avatar label inside profile menu
        WEB.put(
                ElementKey.PROFILE_MENU_AVATAR,
                By.cssSelector("app-profile-menu .profile-avatar")
        );

// My Profile button
        WEB.put(
                ElementKey.MY_PROFILE_BUTTON,
                By.xpath("//rb-dropdown-item[.//div[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'my profile')]]//div[@role='menuitem']")
        );
        WEB.put(ElementKey.USER_MENU,
                By.xpath("//div[@role='button' and contains(@id, 'dropdown-user-menu--button')]"));

// Logged username
        WEB.put(
                ElementKey.PROFILE_NAME,
                By.xpath("//div[@class='myProfile_name']/span")
        );

// Close button
        WEB.put(
                ElementKey.CLOSE_BUTTON,
                By.xpath("//rb-button//button[contains(@class, 'c-button--secondary') and contains(., 'Close')]")
        );
        WEB.put(
                ElementKey.LOGOUT_BUTTON,
                By.xpath("//div[@role='menuitem'][.//div[contains(@class,'dropdown-item-label') and translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='log out']]")
        );
        WEB.put(
                ElementKey.LOGOUT_CONFIRM,
                By.xpath("//button[contains(@class,'c-button--primary')][.//span[contains(@class,'c-button__label') and contains(normalize-space(.), 'Log out')]]")
        );
    }
    public static String getNameFieldLocator(String fieldName) {
            String cleanName = fieldName.trim().toLowerCase();

            if (cleanName.contains(" ")) {
                cleanName = cleanName.split("\\s+")[0];
            }

            String nameXpath = String.format(
                    "//android.widget.EditText[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
                    cleanName
            );

            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

            WebElement nameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(nameXpath)));

            return nameElement.getText().trim();

    }

    public static By getLocator(ElementKey key,String platform){
        Map<ElementKey,By> locators = "web".equalsIgnoreCase(platform) ? WEB : MOBILE_LOCATORS;
        By locator = locators.get(key);
        if(locator==null){
            throw new IllegalArgumentException("Locator not found for key: " + key + " on platform: " + platform);
        }
        return locator;

    }

}