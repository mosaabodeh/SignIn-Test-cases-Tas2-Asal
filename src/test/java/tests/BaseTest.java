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
import utils.AppConfig;
import utils.ConfigReader;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseTest {

    private static AppiumDriverLocalService appiumServer;

    private static final ThreadLocal<String> PLATFORM_THREAD = new ThreadLocal<>();

    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    protected String getPlatform() {
        return PLATFORM_THREAD.get();
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
            System.out.println("✅ Appium server started successfully.");
        } catch (Exception e) {
            System.out.println("ℹ️ Node orchestration server context skipped or already hosted.");
        }
    }

    @DataProvider(name = "multiPlatformProvider", parallel = true)
    public Object[][] extractPlatformsFromTestGroups(Method method) {
        List<Object[]> targetPlatforms = new ArrayList<>();

        if (method.isAnnotationPresent(Test.class)) {
            Test testAnnotation = method.getAnnotation(Test.class);
            String[] groups = testAnnotation.groups();

            boolean hasWeb = false;
            boolean hasAndroid = false;

            for (String group : groups) {
                if (group.equalsIgnoreCase("web") && !hasWeb) {
                    targetPlatforms.add(new Object[]{"web"});
                    hasWeb = true;
                } else if (group.equalsIgnoreCase("android") && !hasAndroid) {
                    targetPlatforms.add(new Object[]{"android"});
                    hasAndroid = true;
                }
            }
        }

        if (targetPlatforms.isEmpty()) {
            targetPlatforms.add(new Object[]{"web"});
        }

        return targetPlatforms.toArray(new Object[0][]);
    }

    protected void initializeExecutionSession(String targetPlatform) {
        final String normalizedPlatform = targetPlatform.toLowerCase().trim();
        PLATFORM_THREAD.set(normalizedPlatform);
        final String env = "web".equals(normalizedPlatform) ? "web_env" : "realdevice";

        System.out.println("🚀 [Lifecycle - Thread: " + Thread.currentThread().getId()
                + "] Switched local platform reference to: " + normalizedPlatform);

        ConfigReader.loadConfig(env + ".properties");
        DriverFactory.initDriver(normalizedPlatform);

        if ("web".equalsIgnoreCase(normalizedPlatform)) {
            getDriver().get(AppConfig.getWebUrl());
        } else {
            cleanMobileAppState();
        }
    }

    private void cleanMobileAppState() {
        final WebDriver currentDriver = getDriver();
        if (currentDriver instanceof InteractsWithApps mobileAppEngine) {
            try {
                String appPackage = AppConfig.getAppPackage();
                mobileAppEngine.terminateApp(appPackage);
                mobileAppEngine.activateApp(appPackage);
                System.out.println("🔄 Mobile app state optimized cleanly.");
            } catch (Exception e) {
                System.out.println("⚠️ App state optimization hook skipped: " + e.getMessage());
            }
        }
    }

    protected void logout() {
        if (getDriver() != null) {
            new LogoutPage(getDriver(), getPlatform()).logOut();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownAfterMethod(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            takeScreenshot(result.getName());
        }
        DriverFactory.quitDriver();
        PLATFORM_THREAD.remove();
    }


    @AfterSuite(alwaysRun = true)
    public void stopAppiumServer() {
        if (appiumServer != null && appiumServer.isRunning()) {
            appiumServer.stop();
            System.out.println("🛑 Appium server stopped safely.");
        }
    }

    public void takeScreenshot(String testName) {
        final WebDriver currentDriver = getDriver();
        if (currentDriver == null) return;
        try {
            final File srcFile = ((TakesScreenshot) currentDriver).getScreenshotAs(OutputType.FILE);
            final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            final String filePath = String.format("%s%sscreenhots%s%s_%s_%s.png",
                    System.getProperty("user.dir"), File.separator, File.separator,
                    testName, getPlatform(), timestamp);
            FileUtils.copyFile(srcFile, new File(filePath));
        } catch (Exception e) {
            System.out.println("⚠️ Frame capture routine encountered an I/O system error.");
        }
    }
}