package HRSD.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {


    private final By registerNewOrgBtnLocator = By.id("RegisterNewOrganizationButton");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void clickRegisterNewOrganization() {

        WebElement registerBtn = fluentWait.until(
                ExpectedConditions.visibilityOfElementLocated(registerNewOrgBtnLocator)
        );

        click(registerBtn);

        System.out.println("'Register New Organization' button clicked.");
    }
}