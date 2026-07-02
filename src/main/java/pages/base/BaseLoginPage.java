package pages.base;

public interface BaseLoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
    boolean isDashboardDisplayed();
    String getErrorMessage();
    void clickContinue();
    boolean verifyUserNameThatLoggedIn(String firstName, String lastName);
    //Delete the logout



}