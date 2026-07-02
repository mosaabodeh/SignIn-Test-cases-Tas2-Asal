package pages.web.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

import java.util.EnumMap;
import java.util.Map;

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
                AppiumBy.androidUIAutomator("new UiSelector().text(\"Continue\")")
        );

        MOBILE_LOCATORS.put(
                ElementKey.LOGIN_BUTTON,
                AppiumBy.androidUIAutomator("new UiSelector().text(\"Sign in\")")
        );

        MOBILE_LOCATORS.put(
                ElementKey.NEWS_DASHBOARD,
                AppiumBy.androidUIAutomator("new UiSelector().description(\"News you follow\")")
        );

        MOBILE_LOCATORS.put(
                ElementKey.NAVIGATION_SETTINGS,
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
                By.xpath("//button[contains(@class,'c-button') and contains(.,'Continue')]"));

        WEB.put(ElementKey.LOGIN_BUTTON,
                By.xpath("//button[@type='submit' or contains(.,'Connect') or contains(.,'Sign')]"));
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