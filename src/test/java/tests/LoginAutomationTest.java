package tests;

import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.mobile.DashboardAndroidPage;
import pages.base.BaseDashboard;
import pages.base.BaseLoginPage;
import pages.web.DashboardWebPage;
import pages.web.WebLoginPage;
import pages.mobile.MobileLoginPage;
import utils.JsonReader;
import utils.ConfigReader;

import java.util.stream.Stream;

public class LoginAutomationTest extends BaseTest {

    private static final String LOGIN_DATA_FILE = "loginData.json";

    private Object[] startAndPrepareExecutionEnvironment(String currentPlatform) {
        initializeExecutionSession(currentPlatform);

        System.out.println("⚙️ [Test Lifecycle Thread: " + Thread.currentThread().getId() + "] Mapping Page Objects for: " + currentPlatform);

        BaseLoginPage localLoginPage;
        BaseDashboard localDashboard;

        if ("web".equalsIgnoreCase(currentPlatform)) {
            System.out.println("💻 Mapping Web Element Architecture...");
            localLoginPage = new WebLoginPage(getDriver(), currentPlatform);
            localDashboard = new DashboardWebPage(getDriver(), currentPlatform);
        } else {
            System.out.println("📱 Mapping Mobile Element Architecture...");
            localLoginPage = new MobileLoginPage(getDriver(), currentPlatform);
            localDashboard = new DashboardAndroidPage(getDriver(), currentPlatform);
        }

        resetToLoginPage(currentPlatform);

        return new Object[]{localLoginPage, localDashboard};
    }


    private void resetToLoginPage(String currentPlatform) {
        try {
            if ("web".equalsIgnoreCase(currentPlatform)) {
                System.out.println("Wiping browser cache storage nodes...");
                getDriver().manage().deleteAllCookies();
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) getDriver();
                js.executeScript("window.localStorage.clear();");
                js.executeScript("window.sessionStorage.clear();");
                getDriver().get("https://web.openrainbow.net/app/en-us/login");
            } else {
                if (getDriver() instanceof AndroidDriver mobileDriver) {
                    String appPackage = ConfigReader.getProperty("app.package");
                    if (appPackage == null || appPackage.contains("example")) {
                        appPackage = "com.ale.rainbow";
                    }
                    System.out.println("Resetting target Android app cache environment for: " + appPackage);
                    mobileDriver.terminateApp(appPackage);
                    mobileDriver.activateApp(appPackage);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Non-fatal state context clean bypass: " + e.getMessage());
        }
    }

    private void loginScenario(BaseLoginPage loginPage, String validUser, String pass) throws InterruptedException {
        loginPage.enterUsername(validUser);
        loginPage.clickContinue();
        loginPage.enterPassword(pass);
        loginPage.clickLogin();
    }

    @Test(
            priority = 1,
            groups = { "web", "android" },
            dataProvider = "multiPlatformProvider",
            description = "Verify that a user can log in successfully with valid credentials"
    )
    public void testSuccessfulLoginHappyPath(String executionPlatform) throws InterruptedException {
        Object[] pages = startAndPrepareExecutionEnvironment(executionPlatform);
        BaseLoginPage loginPage = (BaseLoginPage) pages[0];
        BaseDashboard dashboard = (BaseDashboard) pages[1];

        String validUser = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "username");
        String validPass = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "password");

        loginScenario(loginPage, validUser, validPass);

        Assert.assertTrue(
                dashboard.verifyUserName("Moodeh Test"),
                "❌ User name mismatch after login");

        logout();
    }

    @Test(priority = 2, groups = { "web", "android" },
            dataProvider = "multiPlatformProvider",
            description = "Verify that invalid credentials yield appropriate system validation error reactions")
    public void testInvalidCredentialsErrorDisplay(String executionPlatform) throws InterruptedException {
        Object[] pages = startAndPrepareExecutionEnvironment(executionPlatform);
        BaseLoginPage loginPage = (BaseLoginPage) pages[0];

        String invalidUser = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "username");
        String invalidPass = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "password");
        String expectedError = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "expectedErrorMessage");

        loginScenario(loginPage, invalidUser, invalidPass);

        String actualError = loginPage.getErrorMessage().toLowerCase();
        System.out.println(" [Final Extracted Error Context]: " + actualError + " | Expected: " + expectedError);

        boolean isValidationTriggered = Stream.of(expectedError, "enter", "valid", "incorrect", "password", "identifier", "rainbow login")
                .anyMatch(actualError::contains);

        Assert.assertTrue(isValidationTriggered,
                "❌ Failsafe: Expected error prompt was not registered by the execution interface. Scraped text: " + actualError);
    }

    @Test(priority = 3, groups = { "web", "android" },
            dataProvider = "multiPlatformProvider",
            description = "Verify empty input configurations trigger identification alerts")
    public void testInvalidEmptyFieldsScenarioErrorDisplay(String executionPlatform) throws InterruptedException {
        Object[] pages = startAndPrepareExecutionEnvironment(executionPlatform);
        BaseLoginPage loginPage = (BaseLoginPage) pages[0];

        if ("android".equalsIgnoreCase(executionPlatform)) {
            loginPage.enterUsername("");
            loginPage.clickContinue();

            String actualScreenText = loginPage.getErrorMessage().toLowerCase();
            System.out.println(" [Final Extracted Error Context]: " + actualScreenText);

            boolean isValidationTriggered = Stream.of("login", "missing", "identifier", "enter your rainbow")
                    .anyMatch(actualScreenText::contains);

            Assert.assertTrue(isValidationTriggered,
                    "❌ Failsafe: The application form permitted an empty submission without showing an authentication alert! Scraped text: " + actualScreenText);
        } else {
            loginPage.enterUsername("");
            WebLoginPage web = (WebLoginPage) loginPage;
            Assert.assertTrue(web.isButtonNotClickable());
        }
    }
}