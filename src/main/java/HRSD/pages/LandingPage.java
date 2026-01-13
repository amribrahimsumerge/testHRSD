package HRSD.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LandingPage extends BasePage {


    private final By loginBtnLocator = By.id("loginBtn");

    public LandingPage(WebDriver driver) {
        super(driver);
    }

    public void clickLogin() {

        WebElement loginBtn = driver.findElement(loginBtnLocator);


        waitForVisibility(loginBtn);
        click(loginBtn);

        System.out.println("Login button clicked.");
    }

    public void open(String url) {
        driver.get(url);
    }
}