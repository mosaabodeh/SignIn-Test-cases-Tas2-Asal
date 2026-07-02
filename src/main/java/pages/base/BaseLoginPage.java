package pages.base;

public interface BaseLoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
    boolean isDashboardDisplayed();
    String getErrorMessage();
    void clickContinue();
    //Delete the logout, Verify name


}