package tests;

import drivers.DriverFactory;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ConfigReader;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseTest {

    private static AppiumDriverLocalService appiumServer;
    protected String platform;

    private static final String DEFAULT_STANDALONE_PLATFORM = "android";
    private static final String DEFAULT_FALLBACK_URL = "https://web.openrainbow.net/app/en-us/login";

    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    @BeforeSuite(alwaysRun = true)
    public void startAppiumServer() {
        try {
            final AppiumServiceBuilder builder = new AppiumServiceBuilder()
                    .withIPAddress("127.0.0.1")
                    .usingPort(4723)
                    .withArgument(() -> "--allow-cors");
            appiumServer = AppiumDriverLocalService.buildService(builder);
            appiumServer.start();
            System.out.println(">>> Appium Node Server running isolated socket locally <<<");
        } catch (Exception e) {
            System.out.println("ℹ️ Node orchestration server context skipped or already hosted.");
        }
    }

    @BeforeClass(alwaysRun = true)
    @Parameters({"platform", "environment", "systemPort"})
    public void setUp(@Optional("auto") String targetPlatform,
                      @Optional("auto") String targetEnv,
                      @Optional("8201") String systemPort) {

        final String runningClassName = this.getClass().getSimpleName();
        System.out.println("🚀 [Auto-Detect] Context Target Class Evaluated: " + runningClassName);

        if ("auto".equalsIgnoreCase(targetPlatform)) {
            final String lowercaseClassName = runningClassName.toLowerCase();
            if (lowercaseClassName.contains("web") || lowercaseClassName.contains("browser")) {
                this.platform = "web";
                targetEnv = "web_env";
            } else if (lowercaseClassName.contains("mobile") || lowercaseClassName.contains("android")) {
                this.platform = "android";
                targetEnv = "realdevice";
            } else {
                this.platform = DEFAULT_STANDALONE_PLATFORM;
                targetEnv = "web".equals(this.platform) ? "web_env" : "realdevice";
            }
            System.out.println("🎯 [Auto-Detect] Local Standalone Active! Context Platform forced: " + this.platform);
        } else {
            this.platform = targetPlatform.toLowerCase().trim();
            System.out.println("📋 [XML Config] Distributed Grid Executed! Platform Target resolved: " + this.platform);
        }

        if ("web".equals(this.platform) && "auto".equalsIgnoreCase(targetEnv)) {
            targetEnv = "web_env";
        } else if ("android".equals(this.platform) && "auto".equalsIgnoreCase(targetEnv)) {
            targetEnv = "realdevice";
        }

        ConfigReader.loadConfig(targetEnv + ".properties");
        DriverFactory.initDriver(this.platform);

        if ("web".equalsIgnoreCase(this.platform)) {
            final String webUrl = ConfigReader.getProperty("web.url");
            getDriver().get(webUrl != null ? webUrl : DEFAULT_FALLBACK_URL);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void launchAppCleanly(Method method) {
        final WebDriver currentDriver = getDriver();
        if (currentDriver == null || "web".equalsIgnoreCase(this.platform)) {
            return;
        }

        final String appPackage = ConfigReader.getPropertyOrDefault("app.package", "com.ale.rainbow");

        if (currentDriver instanceof InteractsWithApps mobileAppEngine) {
            try {
                mobileAppEngine.terminateApp(appPackage);
                mobileAppEngine.activateApp(appPackage);
                System.out.println("🔄 Mobile package isolated lifecycle recycled cleanly.");
            } catch (Exception e) {
                System.out.println("⚠️ App state optimization hook was rejected by Appium session context: " + e.getMessage());
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownAfterMethod(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            takeScreenshot(result.getName());
        }
        System.out.println("🔄 >>> Thread Execution Method Step Completed Context.");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void stopAppiumServer() {
        if (appiumServer != null && appiumServer.isRunning()) {
            appiumServer.stop();
            System.out.println(">>> Appium Node Process detached cleanly from local stack <<<");
        }
    }

    public void takeScreenshot(String testName) {
        final WebDriver currentDriver = getDriver();
        if (currentDriver == null) {
            return;
        }
        try {
            final File srcFile = ((TakesScreenshot) currentDriver).getScreenshotAs(OutputType.FILE);
            final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final String filePath = String.format("%s%sscreenshots%s%s_%s.png",
                    System.getProperty("user.dir"), File.separator, File.separator, testName, timestamp);

            FileUtils.copyFile(srcFile, new File(filePath));
            System.out.println("❌ Verification Engine Fault! State Context saved directly to: " + filePath);
        } catch (Exception e) {
            System.out.println("⚠️ Frame capture routine encountered an error mapping system I/O stream.");
        }
    }
}