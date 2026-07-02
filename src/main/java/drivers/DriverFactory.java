package drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ConfigReader;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD = new ThreadLocal<>();
    private static final Duration DEFAULT_IMPLICIT_TIMEOUT = Duration.ofSeconds(10);
    private static final String APP_SERVER_URL = "http://127.0.0.1:4723/";

    private DriverFactory() {
        // Prevent utility class instantiation
    }

    public static WebDriver getDriver() {
        return DRIVER_THREAD.get();
    }

    public static void initDriver(final String platform) {
        Objects.requireNonNull(platform, "Platform string cannot be null when initializing the driver session.");

        if (getDriver() != null) {
            System.out.println("ℹ️ Thread-local session already active. Skipping redundant initialization.");
            return;
        }

        try {
            final WebDriver driver;
            if ("android".equalsIgnoreCase(platform.trim())) {
                System.out.println("📱 Instantiating UIAutomator2 options via configuration assets...");

                final UiAutomator2Options options = new UiAutomator2Options()
                        .setPlatformName(ConfigReader.getPropertyOrDefault("platform.name", "Android"))
                        .setAutomationName(ConfigReader.getPropertyOrDefault("automation.name", "UiAutomator2"))
                        .setDeviceName(ConfigReader.getPropertyOrDefault("device.name", "Android Emulator"))
                        .setAppPackage(ConfigReader.getPropertyOrDefault("app.package", "com.ale.rainbow"))
                        .setAppActivity(ConfigReader.getPropertyOrDefault("app.activity", "com.ale.rainbow.SplashActivity"))
                        .setNoReset(true);

                final URL appiumServerUrl = URI.create(APP_SERVER_URL).toURL();
                System.out.println("📱 Spawning localized Appium Android Driver connection proxy...");
                driver = new AndroidDriver(appiumServerUrl, options);
            } else {
                System.out.println("🌐 Instantiating Engine-Hardened Desktop Chrome instance...");
                driver = new ChromeDriver(getChromeOptions());
                driver.manage().window().maximize();
            }

            driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_TIMEOUT);
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
                System.out.println("🧹 Thread session Context dropped, remote sockets closed cleanly.");
            } catch (Exception e) {
                System.out.println("⚠️ Active driver session manipulation dropped mid-execution: " + e.getMessage());
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