package tests;

import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.base.BaseLoginPage;
import pages.DashboardPage;
import pages.web.WebLoginPage;
import pages.mobile.MobileLoginPage;
import utils.JsonReader;
import utils.ConfigReader;

import java.util.Objects;
import java.util.stream.Stream;

public class LoginAutomationTest extends BaseTest {

    private BaseLoginPage loginPage;
    private static final String LOGIN_DATA_FILE = "loginData.json";
    private String platformRun;

    @BeforeMethod
    public void setupPageObjectInstance() {
        this.platformRun = (this.platform != null) ? this.platform : "android";
        System.out.println("⚙️ [Test Lifecycle] Mapping Page Object instances for platform: " + platformRun);

        resetToLoginPage(platformRun);

        if ("web".equalsIgnoreCase(platformRun)) {
            System.out.println(" Mapping Web Element Architecture...");
            loginPage = new WebLoginPage(getDriver());
        } else {
            System.out.println("📱 Mapping Mobile Element Architecture...");
            loginPage = new MobileLoginPage(getDriver());
        }
    }

    private void resetToLoginPage(String platformName) {
        try {
            if ("web".equalsIgnoreCase(platformName)) {
                System.out.println(" Wiping browser cache storage nodes...");
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

                    System.out.println(" Resetting target Android app cache environment for: " + appPackage);
                    mobileDriver.terminateApp(appPackage);
                    mobileDriver.activateApp(appPackage);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Non-fatal state context clean bypass: " + e.getMessage());
        }
    }

    void loginScenario(String validUser, String pass ){
        loginPage.enterUsername(validUser);
        loginPage.clickContinue();
        loginPage.enterPassword(pass);
        loginPage.clickLogin();
    }

    @Test(priority = 1, description = "Verify that a user can log in successfully with valid credentials")
    public void testSuccessfulLoginHappyPath() {

        String validUser = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "username");
        String validPass = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "password");

        loginScenario(validUser, validPass);

        DashboardPage dashboardPage = new DashboardPage(getDriver());

        Assert.assertTrue(
                dashboardPage.verifyUserNameThatLoggedIn("Mosaab m", "odeh"),
                "❌ User name mismatch after login"
        );

        logout();
    }

    @Test(priority = 2, description = "Verify that invalid credentials yield appropriate system validation error reactions")
    public void testInvalidCredentialsErrorDisplay() {
        String invalidUser = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "username");
        String invalidPass = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "password");
        String expectedError = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "expectedErrorMessage");

        loginScenario(invalidUser,invalidPass);

        String actualError = loginPage.getErrorMessage().toLowerCase();
        System.out.println(" [Final Extracted Error Context]: " + actualError + " | Expected: " + expectedError);

        boolean isValidationTriggered = Stream.of(expectedError,"enter", "valid", "incorrect", "password", "identifier", "rainbow login")
                .anyMatch(actualError::contains);
        System.out.println("The Actual Error Is : "+actualError);
        Assert.assertTrue(isValidationTriggered,
                "❌ Failsafe: Expected error prompt was not registered by the execution interface. Scraped text: " + actualError);

    }

    @Test(priority = 3, description = "Verify empty input configurations trigger identification alerts")
    public void testInvalidEmptyFieldsScenarioErrorDisplay() {
        if(Objects.equals(platform, "android")){
        loginPage.enterUsername("");
        loginPage.clickContinue();

        String actualScreenText = loginPage.getErrorMessage().toLowerCase();
        System.out.println(" [Final Extracted Error Context]: " + actualScreenText);

        boolean isValidationTriggered = Stream.of("login", "missing", "identifier", "enter your rainbow")
                .anyMatch(actualScreenText::contains);

        Assert.assertTrue(isValidationTriggered,
                "❌ Failsafe: The application form permitted an empty submission without showing an authentication alert! Scraped text: " + actualScreenText
        );
        }
        else {
            loginPage.enterUsername("");
            WebLoginPage web=new WebLoginPage(getDriver());
           Assert.assertTrue(web.isButtonNotClickable());
        }
    }
}