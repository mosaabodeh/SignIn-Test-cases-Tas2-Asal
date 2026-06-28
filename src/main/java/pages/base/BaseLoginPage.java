package pages.base;

public interface BaseLoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
    boolean isDashboardDisplayed();
    String getErrorMessage();
    void clickContinue();
    void logOut();
    boolean verifyUserNameThatLoggedIn(String firstName,String lastName);

}