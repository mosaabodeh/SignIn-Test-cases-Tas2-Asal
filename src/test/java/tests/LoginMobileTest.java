package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.base.BaseLoginPage;
import pages.web.WebLoginPage;
import pages.mobile.MobileLoginPage;
import utils.JsonReader;

public class LoginMobileTest extends BaseTest {

    private BaseLoginPage loginPage;
    private static final String LOGIN_DATA_FILE = "loginData.json";

    @BeforeMethod
    @Parameters({"platform"})
    public void setupPageObjectInstance(@Optional("android") String platformName) {
            loginPage = new MobileLoginPage(getDriver());
            System.out.println("🔄 Preparing clean state context before test execution...");
        resetToLoginPage(platformName);
    }


    private void resetToLoginPage(String platformName) {
        try {
            if ("web".equalsIgnoreCase(platformName)) {
                getDriver().manage().deleteAllCookies();
                getDriver().get("https://openrainbow.com/");
            } else {

                if (getDriver() instanceof io.appium.java_client.android.AndroidDriver) {
                    io.appium.java_client.android.AndroidDriver mobileDriver = (io.appium.java_client.android.AndroidDriver) getDriver();
                    String appPackage = "com.example.login";

                    mobileDriver.terminateApp(appPackage);
                    mobileDriver.activateApp(appPackage);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Non-fatal exception triggered during state context wipe: " + e.getMessage());
        }
    }

    @Test(priority = 1, description = "Verify that a user can log in successfully with valid credentials")
    public void testSuccessfulLoginHappyPath() {
        String validUser = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "username");
        String validPass = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "password");

        loginPage.enterUsername(validUser);
        loginPage.clickContinue();
        loginPage.enterPassword(validPass);
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isDashboardDisplayed(),
                "Failsafe: Dashboard landing page failed to mount post-authentication.");
        Assert.assertTrue( loginPage.verifyUserNameThatLoggedIn("Mosaab m", "odeh"),
                "Failsafe: The User name Dosnt Mach With The Same User Loge din Account, failed to mount post-authentication.");
        ((MobileLoginPage) loginPage).logOut();
    }

    @Test(priority = 2, description = "Verify that invalid credentials yield appropriate system validation error reactions")
    public void testInvalidCredentialsErrorDisplay() {
        String invalidUser = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "username");
        String invalidPass = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "password");
        String expectedError = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "expectedErrorMessage");

        loginPage.enterUsername(invalidUser);
        loginPage.clickContinue();
        loginPage.enterPassword(invalidPass);
        loginPage.clickLogin();

        String actualError = loginPage.getErrorMessage();
        System.out.println("The Actual error: " + actualError + " the expected error: " + expectedError);
        Assert.assertNotNull(expectedError);
        Assert.assertTrue(actualError.contains(expectedError),
                "Failsafe: Expected error prompt was not registered by the execution interface.");
    }

    @Test(priority = 3, description = "Verify that empty username fields yield appropriate system validation error reactions")
    public void testInvalidEmptyFieldsScenarioErrorDisplay() {
        String invalidUser = JsonReader.getTestData(LOGIN_DATA_FILE, "emptyFieldsScenario", "username");
        String expectedError = JsonReader.getTestData(LOGIN_DATA_FILE, "emptyFieldsScenario", "expectedErrorMessage");

        loginPage.enterUsername(invalidUser);
        loginPage.clickContinue();

        String actualError = loginPage.getErrorMessage();
        System.out.println("The Actual error: " + actualError + " the expected error: " + expectedError);
        Assert.assertNotNull(expectedError);
        Assert.assertTrue(actualError.contains(expectedError),
                "Failsafe: Expected error prompt was not registered by the execution interface.");
    }
}