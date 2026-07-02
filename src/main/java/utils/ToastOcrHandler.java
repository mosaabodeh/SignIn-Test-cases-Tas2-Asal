package utils;

import io.appium.java_client.AppiumDriver;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class ToastOcrHandler {

    public static String captureAndReadToast(AppiumDriver driver) {
        Tesseract tesseract = new Tesseract();

        String projectRoot = System.getProperty("user.dir");

        String tessdataPath = projectRoot + File.separator + "src"
                + File.separator + "test"
                + File.separator + "resources"
                + File.separator + "tessdata";

        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage("eng");

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destinationFile = new File(projectRoot + File.separator + "target" + File.separator + "toast_screenshot.png");

        try {
            FileUtils.copyFile(screenshot, destinationFile);
            return tesseract.doOCR(destinationFile);
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return "OCR Error: " + e.getMessage();
        }
    }
}