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
import java.util.concurrent.Semaphore;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD = new ThreadLocal<>();


    private static final Semaphore ANDROID_DEVICE_LOCK = new Semaphore(1, true);
    private static final ThreadLocal<Boolean> HOLDS_ANDROID_LOCK = ThreadLocal.withInitial(() -> false);

    private DriverFactory() {}

    public static WebDriver getDriver() {
        return DRIVER_THREAD.get();
    }

    public static void initDriver(final String platform) {
        Objects.requireNonNull(platform, "Platform string cannot be null.");

        if (getDriver() != null) {
            quitDriver();
        }

        final boolean isAndroid = "android".equalsIgnoreCase(platform.trim());

        if (isAndroid) {
            try {
                System.out.println("⏳ [Thread: " + Thread.currentThread().getId()
                        + "] Waiting for exclusive Android device access...");
                ANDROID_DEVICE_LOCK.acquire();
                HOLDS_ANDROID_LOCK.set(true);
                System.out.println("🔓 [Thread: " + Thread.currentThread().getId()
                        + "] Acquired Android device lock.");
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for Android device lock", ie);
            }
        }

        try {
            final WebDriver driver;
            if (isAndroid) {
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
            releaseAndroidLockIfHeld();
            throw new IllegalStateException("❌ Session Context Collapse for platform [" + platform + "]", e);
        }
    }

    public static void quitDriver() {
        final WebDriver driverInstance = getDriver();
        if (driverInstance != null) {
            try {
                driverInstance.quit();
            } finally {
                DRIVER_THREAD.remove();
                releaseAndroidLockIfHeld();
            }
        }
    }

    private static void releaseAndroidLockIfHeld() {
        if (Boolean.TRUE.equals(HOLDS_ANDROID_LOCK.get())) {
            ANDROID_DEVICE_LOCK.release();
            HOLDS_ANDROID_LOCK.set(false);
            System.out.println("🔒 [Thread: " + Thread.currentThread().getId()
                    + "] Released Android device lock.");
        }
    }

    private static ChromeOptions getChromeOptions() {
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US", "--use-fake-ui-for-media-stream", "--disable-blink-features=AutomationControlled");
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("profile.default_content_setting_values.notifications", 1);
        options.setExperimentalOption("prefs", preferences);
        return options;
    }
}