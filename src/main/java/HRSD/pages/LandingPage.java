package HRSD.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LandingPage extends BasePage {


    private final By loginBtnLocator = By.id("loginBtn");

    public LandingPage(WebDriver driver) {
        super(driver);
    }

    public void clickLogin() {
        WebElement loginBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(loginBtnLocator));

        click(loginBtn);

        System.out.println("Login button clicked.");
    }

    public void open(String url) {
        driver.get(url);
    }
}