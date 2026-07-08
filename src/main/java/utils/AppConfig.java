package utils;

public final class AppConfig {

    private AppConfig() {}

    public static String getAppiumServerUrl() {
        return ConfigReader.getPropertyOrDefault("appium.server.url", "http://127.0.0.1:4723/");
    }

    public static String getAppPackage() {
        String appPackage = ConfigReader.getPropertyOrDefault("app.package", "com.ale.rainbow");
        if (appPackage.contains("example")) {
            appPackage = "com.ale.rainbow";
        }
        return appPackage;
    }
    public static String getWebUrl() {
        return ConfigReader.getPropertyOrDefault("web.url", "https://web.openrainbow.net/login");
    }
}