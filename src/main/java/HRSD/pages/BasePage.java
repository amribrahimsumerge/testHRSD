package HRSD.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;

    private final By userGuideBtnLocator = By.id("ss");

    protected Wait<WebDriver> fluentWait;

    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        // In BasePage.java
        this.fluentWait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(30)) // Increased timeout
                .pollingEvery(Duration.ofSeconds(10)) // Increased polling
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(TimeoutException.class);


        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }


    public WebElement waitForElementVisibleWithRetry(String cssSelector) throws InterruptedException {

        int attempts = 0;
        while (attempts < 3) {
            try {
                return fluentWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException|TimeoutException e) {
                attempts++;
                if (attempts == 3) {
                    takeScreenshot("failure_" + cssSelector.replaceAll("[^a-zA-Z0-9]", "_"));
                    logUrlAndDom("failure_" + cssSelector.replaceAll("[^a-zA-Z0-9]", "_"));
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        throw new NoSuchElementException("Element not found after 3 attempts: " + cssSelector);
    }

    public WebElement waitForElementVisibleWithRetryXpath(String xpath) throws InterruptedException {
        int attempts = 0;
        while (attempts < 3) {
            try {
                return fluentWait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))
                );
            } catch (NoSuchElementException | StaleElementReferenceException |
                     ElementNotInteractableException | TimeoutException e) {
                attempts++;
                if (attempts == 3) {
                    takeScreenshot("failure_" + xpath.replaceAll("[^a-zA-Z0-9]", "_"));
                    logUrlAndDom("failure_" + xpath.replaceAll("[^a-zA-Z0-9]", "_"));
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        throw new NoSuchElementException("Element not found after 3 attempts: " + xpath);
    }

    public void clickUserGuideButton() {
        WebElement userGuideBtn = driver.findElement(userGuideBtnLocator);
        waitForVisibility(userGuideBtn);
        waitForClickability(userGuideBtn);
        click(userGuideBtn);
    }

    // Wait for visibility
    protected WebElement waitForVisibility(WebElement element) {
        return fluentWait.until(ExpectedConditions.visibilityOf(element));
    }

    // Wait for clickability
    protected WebElement waitForClickability(WebElement element) {
        return fluentWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    //check if a label is present or not for a specific field
    public boolean isLabelPresentFor(By inputLocator) {
        WebElement inputElement = driver.findElement(inputLocator);
        String inputId = inputElement.getAttribute("id");

        // Check for a <label> element with a 'for' attribute that matches the input's id
        List<WebElement> labels = driver.findElements(By.xpath("//label[@for='" + inputId + "']"));
        return !labels.isEmpty();
    }


    // In BasePage.java
// In BasePage.java
    public void logUrlAndDom(String context) {
        try {
            String url = driver.getCurrentUrl();
            String dom = driver.getPageSource();
            System.out.println("[" + context + "] Current URL: " + url);
            // Optionally, write DOM to a file for large pages
            Files.write(Paths.get("screenshots/" + context + "_dom.html"), dom.getBytes());
        } catch (Exception e) {
            System.out.println("Failed to log URL/DOM: " + e.getMessage());
        }
    }

    public void takeScreenshot(String fileName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File("screenshots/" + fileName + ".png");
            destFile.getParentFile().mkdirs();
            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("Screenshot saved: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Failed to take screenshot: " + e.getMessage());
        }
    }


    public void waitForNetworkIdle(int idleMillis, int timeoutSeconds) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000;
        while (System.currentTimeMillis() < end) {
            Boolean networkIdle = (Boolean) jsExecutor.executeScript(
                    "if (window.pendingRequests === undefined) {" +
                            "  window.pendingRequests = 0;" +
                            "  (function(open, send) {" +
                            "    XMLHttpRequest.prototype.open = function() { window.pendingRequests++; open.apply(this, arguments); };" +
                            "    XMLHttpRequest.prototype.send = function() { " +
                            "      this.addEventListener('loadend', function() { window.pendingRequests--; });" +
                            "      send.apply(this, arguments);" +
                            "    };" +
                            "  })(XMLHttpRequest.prototype.open, XMLHttpRequest.prototype.send);" +
                            "  if (window.fetch) {" +
                            "    var origFetch = window.fetch;" +
                            "    window.fetch = function() { window.pendingRequests++; return origFetch.apply(this, arguments).finally(function() { window.pendingRequests--; }); };" +
                            "  }" +
                            "}" +
                            "return window.pendingRequests === 0;"
            );
            if (networkIdle) {
                try {
                    Thread.sleep(idleMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Boolean stillIdle = (Boolean) jsExecutor.executeScript("return window.pendingRequests === 0;");
                if (stillIdle) return;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new RuntimeException("Network did not become idle within timeout");
    }


    // Scroll to element
    protected void scrollIntoView(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Safe click
    protected void click(WebElement element) {
        waitForClickability(element).click();
    }

    // Safe send keys
    protected void sendKeys(WebElement element, String text) {
        WebElement el = waitForVisibility(element);
        el.clear();
        el.sendKeys(text);
    }

    // Get text with wait
    protected String getText(WebElement element) {
        return waitForVisibility(element).getText();
    }

    // Check if element exists
    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static String getRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    public WebElement getShadowRootElement(ArrayList<String> cssSelectors) throws InterruptedException {

        int attempts = 0;
        while (attempts < 3) {
            try {
                SearchContext shadow = driver.findElement(By.cssSelector(cssSelectors.get(0))).getShadowRoot();
                ArrayList<SearchContext> shadows = new ArrayList<>();
                Thread.sleep(1000);
                shadows.add(shadow);
                for (int i = 0; i < cssSelectors.size() - 2; i++) {
                    shadows.add(shadows.get(i).findElement(By.cssSelector(cssSelectors.get(i + 1))).getShadowRoot());
                    Thread.sleep(1000);
                }
                return shadows.get(cssSelectors.size() - 2)
                        .findElement(By.cssSelector(cssSelectors.get(cssSelectors.size() - 1)));
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException|TimeoutException e) {
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        throw new NoSuchElementException("Shadow root element not found after 3 attempts: " + cssSelectors);
    }


    public WebElement locateElementsInShadowRoot(String Host, String Shadow) throws InterruptedException {

        int attempts = 0;
        while (attempts < 3) {
            try {
                ArrayList<String> Temp = new ArrayList<>();
                fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(Host)));
                Temp.add(Host);
                Temp.add(Shadow);
                return getShadowRootElement(Temp);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException|TimeoutException e) {
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        throw new NoSuchElementException("Shadow root element not found after 3 attempts: Host=" + Host + ", Shadow=" + Shadow);
    }


    public WebElement locateElementInNestedShadowRoots(String... selectors) throws InterruptedException {
        if (selectors == null || selectors.length < 2) {
            throw new IllegalArgumentException("At least two selectors (hosts and target) are required.");
        }
        int attempts = 0;
        while (attempts < 3) {
            try {
                fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selectors[0])));
                ArrayList<String> selectorList = new ArrayList<>();
                for (String selector : selectors) {
                    selectorList.add(selector);
                }
                return getShadowRootElement(selectorList);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException|TimeoutException e) {
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        throw new NoSuchElementException("Nested shadow root element not found after 3 attempts: selectors=" + String.join(", ", selectors));
    }


    // JavaScript click fallback
    protected void jsClick(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    // Pause (useful for debug only)
    protected void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void scrollToElementCss(String css) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement element = driver.findElement(By.cssSelector(css));
        js.executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'});", element);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver ->
                (Boolean) js.executeScript(
                        "return arguments[0].getBoundingClientRect().top >= 0 && arguments[0].getBoundingClientRect().bottom <= window.innerHeight;", element));
    }
}