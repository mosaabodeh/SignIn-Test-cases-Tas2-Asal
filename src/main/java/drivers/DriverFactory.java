package drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.AppConfig;
import utils.ConfigReader;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver getDriver() {
        return DRIVER_THREAD.get();
    }

    public static void initDriver(final String platform) {
        Objects.requireNonNull(platform, "Platform string cannot be null when initializing the driver session.");

        if (getDriver() != null) {
            return;
        }

        try {
            final WebDriver driver;
            if ("android".equalsIgnoreCase(platform.trim())) {
                final UiAutomator2Options options = new UiAutomator2Options()
                        .setPlatformName(ConfigReader.getPropertyOrDefault("platform.name", "Android"))
                        .setAutomationName(ConfigReader.getPropertyOrDefault("automation.name", "UiAutomator2"))
                        .setDeviceName(ConfigReader.getPropertyOrDefault("device.name", "Android Emulator"))
                        .setAppPackage(AppConfig.getAppPackage())
                        .setAppActivity(ConfigReader.getPropertyOrDefault("app.activity", "com.ale.rainbow.SplashActivity"))
                        .setNoReset(true);

                final URL appiumServerUrl = URI.create(AppConfig.getAppiumServerUrl()).toURL();
                driver = new AndroidDriver(appiumServerUrl, options);
            } else {
                driver = new ChromeDriver(getChromeOptions());
                driver.manage().window().maximize();
            }

            DRIVER_THREAD.set(driver);

        } catch (Exception e) {
            throw new IllegalStateException("❌ Factory Engine Collapse: Session failed initialization context for platform [" + platform + "]", e);
        }
    }

    public static void quitDriver() {
        final WebDriver driverInstance = getDriver();
        if (driverInstance != null) {
            try {
                driverInstance.quit();
            } finally {
                DRIVER_THREAD.remove();
            }
        }
    }

    private static ChromeOptions getChromeOptions() {
        final ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--lang=en-US",
                "--use-fake-ui-for-media-stream",
                "--disable-blink-features=AutomationControlled"
        );

        final Map<String, Object> preferences = new HashMap<>();
        preferences.put("profile.default_content_setting_values.media_stream_camera", 1);
        preferences.put("profile.default_content_setting_values.media_stream_mic", 1);
        preferences.put("profile.default_content_setting_values.notifications", 1);
        preferences.put("intl.accept_languages", "en-US,en");
        preferences.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", preferences);
        return options;
    }
}