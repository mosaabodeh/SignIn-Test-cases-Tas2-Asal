package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.base.BaseLoginPage;
import pages.web.WebLoginPage;
import utils.JsonReader;

public class LoginWebTest extends BaseTest {

    private static final String LOGIN_DATA_FILE = "loginData.json";
    private BaseLoginPage loginPage;

    @BeforeMethod
    public void setupPageObjectInstance() {
        if ("web".equalsIgnoreCase(this.platform)) {
            this.loginPage = new WebLoginPage(getDriver());
        } else {
            throw new IllegalArgumentException("❌ Design Mismatch: LoginWebTest execution requires a 'web' target platform.");
        }
    }

    @Test(priority = 1, description = "Verify that a user can log in successfully with valid credentials")
    public void testSuccessfulLoginHappyPath()  {
        String validUser = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "username");
        String validPass = JsonReader.getTestData(LOGIN_DATA_FILE, "validLoginScenario", "password");

        loginPage.enterUsername(validUser);
        loginPage.clickContinue();
        loginPage.enterPassword(validPass);
        loginPage.clickLogin();

        Assert.assertTrue( loginPage.verifyUserNameThatLoggedIn("Mosaab m","odeh"),
                "❌ Failsafe: Logged-in user name mismatch on Web platform.");
        loginPage.logOut();
    }
    @Test(priority = 2, description = "Verify web user receives correct error response with invalid credentials")
    public void testUnsuccessfulLoginWithInvalidCredentials() {
        String username = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "username");
        String password = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "password");
        String expectedError = JsonReader.getTestData(LOGIN_DATA_FILE, "invalidLoginScenario", "expectedErrorMessage");

        loginPage.enterUsername(username);
        loginPage.clickContinue();
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        String actualError = loginPage.getErrorMessage();
        Assert.assertEquals(actualError, expectedError,
                "❌ Verification Error: The invalid login warning banner message text did not match.");
    }


}