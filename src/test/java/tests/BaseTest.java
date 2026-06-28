package tests;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ConfigReader;
import drivers.DriverFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class BaseTest {

    private static AppiumDriverLocalService server;
    public String platform;

    // 🚀 SINGLE SEED ENGINE CONTROL HOOK: Change to "web" or "android" for direct play runs!
    private static final String DEFAULT_STANDALONE_PLATFORM = "android";

    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    @BeforeSuite
    public void startAppiumServer() {
        try {
            AppiumServiceBuilder builder = new AppiumServiceBuilder()
                    .withIPAddress("127.0.0.1")
                    .usingPort(4723)
                    .withArgument(() -> "--allow-cors");
            server = AppiumDriverLocalService.buildService(builder);
            server.start();
            System.out.println(">>> Appium Server started automatically <<<");
        } catch (Exception e) {
            System.out.println("ℹ️ Appium server startup skipped or already active.");
        }
    }

    @BeforeClass
    @Parameters({"platform", "environment", "systemPort"})
    public void setUp(@Optional("auto") String targetPlatform,
                      @Optional("auto") String targetEnv,
                      @Optional("8201") String systemPort) throws MalformedURLException {

        String runningClassName = this.getClass().getSimpleName();
        System.out.println("🚀 [Auto-Detect] Running Test Class: " + runningClassName);

        if ("auto".equalsIgnoreCase(targetPlatform)) {
            String lowercaseClassName = runningClassName.toLowerCase();
            if (lowercaseClassName.contains("web") || lowercaseClassName.contains("browser")) {
                this.platform = "web";
                targetEnv = "web_env";
            } else if (lowercaseClassName.contains("mobile") || lowercaseClassName.contains("android")) {
                this.platform = "android";
                targetEnv = "realdevice";
            } else {
                this.platform = DEFAULT_STANDALONE_PLATFORM.toLowerCase();
                targetEnv = "web".equals(this.platform) ? "web_env" : "realdevice";
            }
            System.out.println("🤖 [Auto-Detect] Standalone Run Detected! Setting Platform to: " + this.platform);
        } else {
            this.platform = targetPlatform.toLowerCase();
            System.out.println("📋 [XML Config] Suite Parameter Detected! Setting Platform to: " + this.platform);
        }

        if ("web".equals(this.platform) && "auto".equalsIgnoreCase(targetEnv)) {
            targetEnv = "web_env";
        } else if ("android".equals(this.platform) && "auto".equalsIgnoreCase(targetEnv)) {
            targetEnv = "realdevice";
        }

        // 🚀 ROUTING INITIALIZATION EXCLUSIVELY VIA THE CENTRAL FACTORY
        DriverFactory.initDriver(this.platform);

        if ("web".equalsIgnoreCase(this.platform)) {
            try {
                ConfigReader.loadConfig(targetEnv + ".properties");
                String webUrl = ConfigReader.getProperty("web.url");
                getDriver().get(webUrl != null ? webUrl : "https://web.openrainbow.net/app/en-us/login");
            } catch (Exception e) {
                System.out.println("ℹ️ Falling back to default English deep-link path.");
                getDriver().get("https://web.openrainbow.net/app/en-us/login");
            }
        }
    }

    @BeforeMethod
    public void launchAppCleanly(Method method) {
        if (getDriver() == null || "web".equalsIgnoreCase(this.platform)) return;

        String appPackage = ConfigReader.getProperty("app.package");
        if (appPackage == null) appPackage = "com.example.login";

        try {
            WebDriver driverInstance = getDriver();
            Method terminateAppMethod = driverInstance.getClass().getMethod("terminateApp", String.class);
            Method activateAppMethod = driverInstance.getClass().getMethod("activateApp", String.class);

            terminateAppMethod.invoke(driverInstance, appPackage);
            activateAppMethod.invoke(driverInstance, appPackage);
            System.out.println("🔄 Mobile application package state cycled cleanly.");
        } catch (Exception e) {
            System.out.println("⚠️ Mobile application state refresh skipped: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDownAfterMethod(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            takeScreenshot(result.getName());
        }
        System.out.println("🔄 >>> Test Method Finished.");
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @AfterSuite
    public void stopAppiumServer() {
        if (server != null && server.isRunning()) {
            server.stop();
            System.out.println(">>> Appium Server stopped automatically <<<");
        }
    }

    public void takeScreenshot(String testName) {
        if (getDriver() == null) return;
        try {
            File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filePath = System.getProperty("user.dir") + File.separator + "screenshots" + File.separator + testName + "_" + timestamp + ".png";
            FileUtils.copyFile(srcFile, new File(filePath));
            System.out.println("❌ Test Failed! Screenshot captured at: " + filePath);
        } catch (Exception e) {
            System.out.println("⚠️ Screenshot failed.");
        }
    }
}