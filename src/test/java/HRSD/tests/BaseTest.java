package HRSD.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;

public class BaseTest {

    protected WebDriver driver;

    // Changed to @BeforeClass so it runs once before all tests in this class
    @BeforeClass
    public void setUp() {
        String driverPath = "C:/Users/aragab/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe";

        File file = new File(driverPath);
        if (!file.exists()) {
            throw new RuntimeException("ChromeDriver not found at: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");

        // Fix for HTTPS/SSL errors
        options.setAcceptInsecureCerts(true);
        options.addArguments("--ignore-certificate-errors");

        driver = new ChromeDriver(options);
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
//            driver.quit();
        }
    }
}