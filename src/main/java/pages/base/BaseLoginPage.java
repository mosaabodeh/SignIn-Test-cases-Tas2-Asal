package pages.base;

public interface BaseLoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin() throws InterruptedException;
    String getErrorMessage();
    void clickContinue() throws InterruptedException;




}