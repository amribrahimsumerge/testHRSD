package HRSD.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.Random;

public class RegistrationPage extends BasePage {

    private final String ARABIC_HOST = "#OrganizationArabicNameField";
    private final String ENGLISH_HOST = "#OrganizationEnglishNameField";
    private final String NUMBER_700_HOST = "#Number700Field";
    private final String ORIENTATION_HOST = "#OrganizationOrientationField";
    private final String SLOGAN_HOST = "#OrganizationSloganButton";
    private final String PHONE_HOST = "#OrganizationPhoneField";
    private final String REP_NAME_HOST = "#OrganizationRepresentativeNameField";
    private final String REP_ID_HOST = "#OrganizationRepresentativeIDField";
    private final String REP_PHONE_HOST = "#OrganizationRepresentativePhoneField";
    private final String POSITION_HOST = "[formcontrolname='applicantPosition']";
    private final String FOUNDATION_HOST = "#FoundationDateField";
    private final String DOMAIN_HOST = "#DomainField";
    private final String EMAIL_HOST = "#EmailDomainField";
    private final String VERIFICATION_HOST = "#VerificationCodeField";
    private final String REGISTER_BTN_HOST = "#RegisterButton";
    private final String REGISTER_BTN_TARGET = "button[type='submit']";

    private final By CONFIRM_BUTTON = By.id("ConfirmButton");

    private final String MSG_HOST_OUTER = "dga-alert[class='alert hydrated']";

    private final String MSG_HOST_INNER = "dga-message[class='alert-content hydrated']";

    private final String MSG_TARGET = "dga-text[class='content dga_body_l_regular success_strong hydrated']";

    private String generatedDomainName = "";

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public void completeFullRegistration(String arabicName, String englishName) throws InterruptedException {
        System.out.println("Starting full registration flow.");
        Random random = new Random();

        fillInputInShadow(ARABIC_HOST, "input[placeholder='اسم المنشأة بالعربية']", arabicName);
        fillInputInShadow(ENGLISH_HOST, "input[placeholder='اسم المنشأة بالإنجليزية']", englishName);

        String num700 = "700" + getRandomDigits(7);
        fillInputInShadow(NUMBER_700_HOST, "input[placeholder='الرقم الوطني الموحد']", num700);
        fillInputInShadow(ORIENTATION_HOST, "input[placeholder='توجه المنشأة']", "خاص");

        String imagePath = "C:\\Users\\aragab\\Pictures\\Screenshots\\logo.png";
        uploadFileInShadow(SLOGAN_HOST, imagePath);

        String phone = "56" + getRandomDigits(7);
        fillInputInShadow(PHONE_HOST, "input[placeholder='111234567']", phone);
        fillInputInShadow(REP_NAME_HOST, "input[placeholder='اسم مقدم الطلب']", "عمرو ابراهيم");

        String startDigit = "1";
        String repID = startDigit + getRandomDigits(9);
        fillInputInShadow(REP_ID_HOST, "input[placeholder='الهوية الوطنية لمقدم الطلب']", repID);

        String repPhone = "56" + getRandomDigits(7);
        fillInputInShadow(REP_PHONE_HOST, "input[placeholder='512345678']", repPhone);
        fillInputInShadow(POSITION_HOST, "input[placeholder='المنصب الوظيفي لمقدم الطلب']", "مهندس");
        fillInputInShadow(FOUNDATION_HOST, "input[placeholder='DD/MM/YYYY']", "01/01/2024");

        String randomDomainName = getRandomString(5).toLowerCase();
        this.generatedDomainName = randomDomainName;
        String fullDomainText = "@" + randomDomainName + ".com";
        fillInputInShadow(DOMAIN_HOST, "input[placeholder='@example.com']", fullDomainText);

        String emailPrefix = getRandomString(4).toLowerCase();
        String fullEmail = emailPrefix + "@" + this.generatedDomainName + ".com";
        fillInputInShadow(EMAIL_HOST, "input[placeholder='someone@example.com']", fullEmail);

        System.out.println("Attempting to click Register Button...");
        WebElement registerBtn = locateElementsInShadowRoot(REGISTER_BTN_HOST, REGISTER_BTN_TARGET);

        if (registerBtn != null) {
            jsClick(registerBtn);
            System.out.println("Clicked Register Button.");
        } else {
            throw new RuntimeException("Could not find Register Button.");
        }

        System.out.println("Entering Verification Code...");
        fillInputInShadow(VERIFICATION_HOST, "[id='-0']", "5");
        fillInputInShadow(VERIFICATION_HOST, "[id='-1']", "5");
        fillInputInShadow(VERIFICATION_HOST, "[id='-2']", "5");
        fillInputInShadow(VERIFICATION_HOST, "[id='-3']", "5");

        WebElement confirmBtn = fluentWait.until(ExpectedConditions.elementToBeClickable(CONFIRM_BUTTON));
        click(confirmBtn);
        System.out.println("Clicked Confirm Button.");

        String expectedMessage = "تم إرسال بريد إلكتروني يحتوي على رقم الطلب";
        System.out.println("Waiting for success message: " + expectedMessage);

        try {
            WebElement msgElement = locateElementInNestedShadowRoots(
                    MSG_HOST_OUTER,
                    MSG_HOST_INNER,
                    MSG_TARGET
            );
            scrollIntoView(msgElement);
            String actualText = msgElement.getText();
            System.out.println("Found Message Text: " + actualText);

            Assert.assertTrue(actualText.contains(expectedMessage),
                    "Success message mismatch! Expected part: '" + expectedMessage + "', Found: '" + actualText + "'");

            System.out.println("Assertion Passed: Success message verified.");

        } catch (Exception e) {
            throw new RuntimeException("Success message verification failed.", e);
        }
    }

    private void fillInputInShadow(String hostSelector, String targetSelector, String value) throws InterruptedException {
        WebElement element = locateElementsInShadowRoot(hostSelector, targetSelector);
        if (element != null) {
            waitForVisibility(element);
            element.clear();
            element.sendKeys(value);
        } else {
            throw new RuntimeException("Could not find element: Host=" + hostSelector + ", Target=" + targetSelector);
        }
    }

    private void uploadFileInShadow(String hostSelector, String filePath) throws InterruptedException {
        WebElement fileInput = locateElementsInShadowRoot(hostSelector, "input[type='file']");
        if (fileInput != null) {
            fileInput.sendKeys(filePath);
        } else {
            WebElement customBtn = locateElementsInShadowRoot(hostSelector, ".single-button");
            if (customBtn != null) {
                customBtn.sendKeys(filePath);
            } else {
                throw new RuntimeException("Could not locate file upload input in " + hostSelector);
            }
        }
    }

    private String getRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}