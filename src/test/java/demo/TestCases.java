package demo;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestCases {
    ChromeDriver driver;

    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment.
     */
    @SuppressWarnings("null")
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Start Test case: Google Form");

        // Navigate to the provided Google Form link.
        driver.get("https://forms.gle/wjPkzeSEk1CM7KgGA");
        Thread.sleep(8000);

        try {
            // Fill in the name field with the expected learner name.
            writeText(driver, By.xpath("//div[contains(@class,'Xb9hP')]/input[@type='text']"), "Crio Learner");

            // Write the QA goal with the current epoch value as part of the answer.
            writeText(driver, By.xpath("//textarea[@aria-label='Your answer']"),
                    "I want to be the best QA Engineer! " + calculateEpochTimeToString(0));

            // Select the experience option from the radio choices.
            clickCheckBox(driver, By.xpath(
                    "(//span[normalize-space(text()) = 'How much experience do you have in Automation Testing?']/ancestor::div[4]//div[@class='AB7Lab Id5V1'])[2]"));

            // Select the automation tools that were learned in the course.
            clickCheckBox(driver, By.xpath(
                    "(//*[normalize-space(text()) = 'Which of the following have you learned in Crio.Do for Automation Testing?']/ancestor::div[4]//div[@class='uHMk6b fsHoPb'])[1]"));
            clickCheckBox(driver, By.xpath(
                    "(//*[normalize-space(text()) = 'Which of the following have you learned in Crio.Do for Automation Testing?']/ancestor::div[4]//div[@class='uHMk6b fsHoPb'])[2]"));
            clickCheckBox(driver, By.xpath(
                    "(//*[normalize-space(text()) = 'Which of the following have you learned in Crio.Do for Automation Testing?']/ancestor::div[4]//div[@class='uHMk6b fsHoPb'])[4]"));

            // Choose the preferred salutation from the dropdown.
            selectFromDropdown(driver, By.xpath(
                    "//*[normalize-space(text()) = 'How should you be addressed?']/ancestor::div[4]//div[@class='MocG8c HZ3kWc mhLiyf LMgvRb KKjvXb DEh1R']"), "Mr");

            // Enter the date from seven days ago in the date field.
            writeText(driver, By.xpath(
                    "//*[normalize-space(text()) = 'What was the date 7 days ago?']/ancestor::div[4]//input[@class='whsOnd zHQkBf']"),
                    calculateDateTimeToString("dd/MM/yyyy", 6.048e8));

            // Enter the specific time expected in the assignment: 07:30.
            writeText(driver, By.xpath(
                    "(//*[normalize-space(text()) = 'What is the time right now?']/ancestor::div[4]//input[@class='whsOnd zHQkBf'])[1]"),
                    "07");
            writeText(driver, By.xpath(
                    "(//*[normalize-space(text()) = 'What is the time right now?']/ancestor::div[4]//input[@class='whsOnd zHQkBf'])[2]"),
                    "30");

            // Submit the form and verify the completion message.
            driver.findElement(By.xpath("//*[normalize-space(text())='Submit']/ancestor::div[1]")).click();
            Thread.sleep(3000);
            String confirmationText = driver.findElement(By.xpath("//div[@role='heading']/../div[3]")).getText();
            System.out.println(confirmationText);
            Assert.assertTrue(confirmationText.contains("Thanks for your response"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failure!");
            Assert.fail("Google form automation failed: " + e.getMessage());
        }

        System.out.println("End Test case: Google Form");
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();
    }

    private static void writeText(ChromeDriver driver, By selector, String textToSend) throws Exception {
        System.out.println("Trying to send text - " + textToSend);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
        WebElement element = driver.findElement(selector);
        element.clear();
        element.sendKeys(textToSend);
        Thread.sleep(2000);
        System.out.println("Success!");
    }

    private static String calculateDateTimeToString(String formatString, double offsetInMs) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = (long) (offsetInMs / 1000);
        long nanos = (long) ((offsetInMs % 1000) * 1000000);
        LocalDateTime newDateTime = now.minus(Duration.ofSeconds(seconds, nanos));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
        return newDateTime.format(formatter);
    }

    private static String calculateEpochTimeToString(int offsetInMs) {
        Instant now = Instant.now();
        Instant newInstant = now.plusMillis(offsetInMs);
        return String.valueOf(newInstant.toEpochMilli());
    }

    private static void clickCheckBox(ChromeDriver driver, By selector) throws Exception {
        System.out.println("Trying to check boxes or radio buttons");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
        WebElement element = driver.findElement(selector);
        element.click();
        Thread.sleep(2000);
        System.out.println("Success!");
    }

    private static void selectFromDropdown(ChromeDriver driver, By dropDownSelector, String textToSelect)
            throws Exception {
        System.out.println("Trying to select from dropdown - " + textToSelect);
        WebElement dropdownElement = driver.findElement(dropDownSelector);
        dropdownElement.click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("(//div[@data-value='" + textToSelect + "'])[2]")).click();
        Thread.sleep(2000);
        System.out.println("Success!");
    }

    private static void handleAlert(ChromeDriver driver, Boolean confirm) throws InterruptedException {
        System.out.println("Trying to handle alert");
        Alert alert = driver.switchTo().alert();
        Thread.sleep(2000);

        if (confirm) {
            alert.accept();
        } else {
            alert.dismiss();
        }
        System.out.println("Success!");
    }
}