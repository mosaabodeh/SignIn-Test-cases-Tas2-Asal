package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    public static void loadConfig(String fileName) {
        try {
            properties = new Properties();
            String path = "src/test/resources/" + fileName;
            System.out.println("⚙️ [Config] Loading properties file from: " + path);

            FileInputStream input = new FileInputStream(path);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            System.err.println("❌ [Config Error] Failed to read target file: " + fileName);
            throw new RuntimeException("Could not load properties file: " + fileName);
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            String currentRunningTest = Thread.currentThread().getStackTrace()[2].getClassName();
            if (currentRunningTest.toLowerCase().contains("web") || currentRunningTest.toLowerCase().contains("browser")) {
                loadConfig("web_env.properties");
            } else {
                loadConfig("realdevice.properties");
            }
        }
        return properties.getProperty(key);
    }
}