package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigReader {

    private static final ThreadLocal<Properties> THREAD_PROPERTIES = new ThreadLocal<>();

    private ConfigReader() {
    }


    public static void loadConfig(final String fileName) {
        final String path = System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "test"
                + File.separator + "resources"
                + File.separator + fileName;

        System.out.println("⚙️ [Config Engine] Mapping memory buffers for environment configuration: " + path);

        try (FileInputStream input = new FileInputStream(path)) {
            final Properties localizedProps = new Properties();
            localizedProps.load(input);
            THREAD_PROPERTIES.set(localizedProps);
        } catch (IOException e) {
            System.err.println("❌ [Config Error] Target property storage file inaccessible: " + fileName);
            throw new IllegalStateException("Automation Engine Halt: Unable to process properties file context -> " + fileName, e);
        }
    }


    public static String getProperty(final String key) {
        if (THREAD_PROPERTIES.get() == null) {
            System.out.println("ℹ️ Thread property bundle not explicit. Invoking dynamic stack tracing fallback analysis...");
            autoLoadMissingConfig();
        }
        return THREAD_PROPERTIES.get().getProperty(key);
    }

    public static String getPropertyOrDefault(final String key, final String defaultValue) {
        final String value = getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
static void autoLoadMissingConfig() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String contextClass = "";

        for (StackTraceElement element : stackTrace) {
            final String name = element.getClassName().toLowerCase();
            if (name.contains("tests.") || name.contains("pages.")) {
                contextClass = name;
                break;
            }
        }

        if (contextClass.contains("web") || contextClass.contains("browser")) {
            loadConfig("web_env.properties");
        } else {
            loadConfig("realdevice.properties");
        }
    }
}