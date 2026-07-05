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
import pages.LogoutPage;
import pages.web.WebLoginPage;
import utils.AppConfig;
import utils.ConfigReader;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseTest {

    private static AppiumDriverLocalService appiumServer;
    protected String platform;

    private static final String DEFAULT_STANDALONE_PLATFORM = "android";

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
        } else {
            this.platform = targetPlatform.toLowerCase().trim();
        }

        if ("web".equals(this.platform) && "auto".equalsIgnoreCase(targetEnv)) {
            targetEnv = "web_env";
        } else if ("android".equals(this.platform) && "auto".equalsIgnoreCase(targetEnv)) {
            targetEnv = "realdevice";
        }

        ConfigReader.loadConfig(targetEnv + ".properties");
        DriverFactory.initDriver(this.platform);

        if ("web".equalsIgnoreCase(this.platform)) {
            getDriver().get(AppConfig.getWebUrl());
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void launchAppCleanly(Method method) {
        final WebDriver currentDriver = getDriver();
        if (currentDriver == null || "web".equalsIgnoreCase(this.platform)) {
            return;
        }

        if (currentDriver instanceof InteractsWithApps mobileAppEngine) {
            try {
                String appPackage = AppConfig.getAppPackage();
                mobileAppEngine.terminateApp(appPackage);
                mobileAppEngine.activateApp(appPackage);
            } catch (Exception e) {
                System.out.println("⚠️ App state optimization hook skipped: " + e.getMessage());
            }
        }
    }
    protected void logout() {
        new LogoutPage(getDriver()).logOut();

    }
    @AfterMethod(alwaysRun = true)
    public void tearDownAfterMethod(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            takeScreenshot(result.getName());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void stopAppiumServer() {
        if (appiumServer != null && appiumServer.isRunning()) {
            appiumServer.stop();
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
        } catch (Exception e) {
            System.out.println("⚠️ Frame capture routine encountered an I/O system error.");
        }
    }
}