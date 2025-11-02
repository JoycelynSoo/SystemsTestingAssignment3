import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RegistrationTests {
    // Prefer setting this via -Dwebdriver.chrome.driver at runtime. Fallback to this path.
    private static final String DEFAULT_CHROME_DRIVER = "/Users/joycelyn.soo/Documents/UTS/Y3S2/SeleniumTest/src/Driver/chromedriver";
    private static final String BASE_URL = "https://ecommerce-playground.lambdatest.io/index.php?route=common/home";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    interface TestCase {
        void run() throws Exception;
    }

    static class NamedTest {
        final String name;
        final TestCase test;

        NamedTest(String name, TestCase test) {
            this.name = name;
            this.test = test;
        }
    }

    // --- Helpers used by tests ---
    static WebDriver createDriver() {
        String driverPath = System.getProperty("webdriver.chrome.driver", DEFAULT_CHROME_DRIVER);
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless=new"); // enable if you want headless

        return new ChromeDriver(options);
    }

    static void goToHome(WebDriver driver) {
        driver.get(BASE_URL);
    }

    static void openRegister(WebDriver driver, WebDriverWait wait) {
        WebElement myAccount = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(.,'My Account') or contains(@title,'My Account')]")
        ));
        new Actions(driver).moveToElement(myAccount).perform();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Register"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstname")));
    }

    // --- Test implementations ---
    static TestCase testHomeLoads = () -> {
        WebDriver driver = createDriver();
        try {
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            goToHome(driver);
            WebElement myAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(.,'My Account')]")
            ));
            if (!myAccount.isDisplayed()) throw new AssertionError("My Account not visible");
        } finally {
            driver.quit();
        }
    };

    static TestCase testRegisterSuccess = () -> {
        WebDriver driver = createDriver();
        try {
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            goToHome(driver);
            openRegister(driver, wait);

            String email = "jane.doe." + System.currentTimeMillis() + "@example.com";

            driver.findElement(By.name("firstname")).sendKeys("Jane");
            driver.findElement(By.name("lastname")).sendKeys("Doe");
            driver.findElement(By.name("email")).sendKeys(email);
            driver.findElement(By.name("telephone")).sendKeys("0400000000");
            driver.findElement(By.name("password")).sendKeys("1234");
            driver.findElement(By.name("confirm")).sendKeys("1234");

            // subscribe No if present
            try {
                WebElement subscribeNo = driver.findElement(By.xpath("//input[@name='newsletter' and (@value='0' or not(@value))]"));
                if (!subscribeNo.isSelected()) subscribeNo.click();
            } catch (Exception ignored) {
            }

            WebElement agree = driver.findElement(By.name("agree"));
            if (!agree.isSelected()) agree.click();

            driver.findElement(By.xpath("//input[@value='Continue' or @type='submit']")).click();

            // wait for some success indicator
            boolean created = wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Your Account Has Been Created') or contains(.,'success')]")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert, .text-success"))
            )) != null;

            if (!created) throw new AssertionError("Account creation not confirmed");
        } finally {
            driver.quit();
        }
    };

    static TestCase testRegisterMissingFirstName = () -> {
        WebDriver driver = createDriver();
        try {
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            goToHome(driver);
            openRegister(driver, wait);

            // leave firstname blank
            driver.findElement(By.name("lastname")).sendKeys("Doe");
            driver.findElement(By.name("email")).sendKeys("jane.missing.firstname." + System.currentTimeMillis() + "@example.com");
            driver.findElement(By.name("telephone")).sendKeys("0400000000");
            driver.findElement(By.name("password")).sendKeys("1234");
            driver.findElement(By.name("confirm")).sendKeys("1234");

            WebElement agree = driver.findElement(By.name("agree"));
            if (!agree.isSelected()) agree.click();

            driver.findElement(By.xpath("//input[@value='Continue' or @type='submit']")).click();

            // Expect a validation error about first name
            WebElement err = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".text-danger, .alert-danger, .warning")
            ));

            if (err == null) throw new AssertionError("Expected validation error but none found");
        } finally {
            driver.quit();
        }
    };

    // --- Simple runner ---
    public static void main(String[] args) {
        List<NamedTest> tests = new ArrayList<>();
        tests.add(new NamedTest("HomeLoads", testHomeLoads));
        tests.add(new NamedTest("RegisterSuccess", testRegisterSuccess));
        tests.add(new NamedTest("RegisterMissingFirstName", testRegisterMissingFirstName));

        AtomicInteger passed = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        System.out.println("Running " + tests.size() + " tests (no test framework)");

        for (NamedTest nt : tests) {
            System.out.println("--- RUN: " + nt.name);
            long start = System.currentTimeMillis();
            try {
                nt.test.run();
                long duration = System.currentTimeMillis() - start;
                System.out.println("--- PASS: " + nt.name + " (" + duration + " ms)");
                passed.incrementAndGet();
            } catch (AssertionError ae) {
                long duration = System.currentTimeMillis() - start;
                System.out.println("--- FAIL: " + nt.name + " (" + duration + " ms) -> " + ae.getMessage());
                failed.incrementAndGet();
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - start;
                System.out.println("--- ERROR: " + nt.name + " (" + duration + " ms)");
                e.printStackTrace(System.out);
                failed.incrementAndGet();
            }
        }

        System.out.println("\nSummary: " + passed.get() + " passed, " + failed.get() + " failed");
        if (failed.get() > 0) System.exit(1);
    }
}
