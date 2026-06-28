package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ConfigReader;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverThread.get();
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--lang=en-US");
        options.addArguments("--use-fake-ui-for-media-stream");
        options.addArguments("--disable-blink-features=AutomationControlled");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
        prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
        prefs.put("profile.default_content_setting_values.notifications", 1);
        prefs.put("intl.accept_languages", "en-US,en"); // Force English headers
        prefs.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", prefs);

        return options;
    }

    public static WebDriver createWebDriver() {
        System.out.println("🌐 Launching Pure Chrome Browser Session (Forced English Mode)...");
        WebDriver driver = new ChromeDriver(getChromeOptions());
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    public static void initDriver(String platform) {
        if (getDriver() == null) {
            WebDriver driver;
            try {
                if (platform.equalsIgnoreCase("android")) {
                    System.out.println("📱 Instantiating Mobile Options dynamically via Config Properties...");

                    io.appium.java_client.android.options.UiAutomator2Options options =
                            new io.appium.java_client.android.options.UiAutomator2Options()
                                    .setPlatformName(ConfigReader.getProperty("platform.name"))
                                    .setAutomationName(ConfigReader.getProperty("automation.name"))
                                    .setDeviceName(ConfigReader.getProperty("device.name"))
                                    .setAppPackage(ConfigReader.getProperty("app.package"))
                                    .setAppActivity(ConfigReader.getProperty("app.activity"))
                                    .setNoReset(true);

                    URL appiumServerUrl = URI.create("http://127.0.0.1:4723/").toURL();
                    System.out.println("📱 Instantiating Mobile Driver safely via native class allocation...");

                    driver = new io.appium.java_client.android.AndroidDriver(appiumServerUrl, options);
                } else {
                    System.out.println("🌐 Instantiating Desktop Chrome Driver...");
                    driver = new ChromeDriver(getChromeOptions());
                    driver.manage().window().maximize();
                }

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driverThread.set(driver);

            } catch (Exception e) {
                throw new RuntimeException("❌ Critical Factory Error: Failed to initialize driver for platform: " + platform, e);
            }
        }
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            try {
                getDriver().quit();
                System.out.println("🧹 Driver session torn down cleanly.");
            } catch (Exception e) {
                System.out.println("⚠️ Warning: Driver session was already dead.");
            } finally {
                driverThread.remove();
            }
        }
    }
}