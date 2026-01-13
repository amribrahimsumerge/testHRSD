package HRSD.tests;

import HRSD.pages.LandingPage;
import HRSD.pages.LoginPage;
import HRSD.pages.RegistrationPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private LandingPage landingPage;
    private LoginPage loginPage;
    private RegistrationPage registrationPage;

    @Test(priority = 1)
    public void testLogin() {
        landingPage = new LandingPage(driver);
        landingPage.open("https://stg-dev.hrsdportal.hrsd.gov.sa/");
        landingPage.clickLogin();
    }

    @Test(priority = 2, dependsOnMethods = "testLogin")
    public void testRegisterNewOrganization() {
        loginPage = new LoginPage(driver);
        loginPage.clickRegisterNewOrganization();
    }

    @Test(priority = 3, dependsOnMethods = "testRegisterNewOrganization")
    public void testCompleteRegistrationFlow() throws InterruptedException {
        registrationPage = new RegistrationPage(driver);

        String arName = "منشأة سامرج جروب ";
        String enName = "Sumerge group";


        registrationPage.completeFullRegistration(arName, enName);

//        Thread.sleep(5000);
    }
}