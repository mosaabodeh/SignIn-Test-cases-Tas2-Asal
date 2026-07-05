package pages.base;

public interface BaseLoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
    String getErrorMessage();
    void clickContinue();




}