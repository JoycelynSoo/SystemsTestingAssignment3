import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
    private static final String BASE_URL = "https://ecommerce-playground.lambdatest.io/index.php?route=common/home";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    interface TestCase {
        String getName();

        void run() throws Exception;
    }

    // TC001: Hover over My Account and click Register
    static TestCase registerAccount = new TestCase() {
        public String getName() {
            return "TC001 - Register Account";
        }

        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get(BASE_URL);
                driver.manage().window().maximize();

                WebElement myAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[contains(@class,'dropdown-toggle') and .//span[contains(text(),'My account')]]")));

                Actions actions = new Actions(driver);
                actions.moveToElement(myAccount).perform();

                WebElement registerLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Register']")));
                registerLink.click();

                wait.until(ExpectedConditions.titleContains("Register Account"));
                System.out.println("Navigated to Register page successfully!");
            } finally {
                driver.quit();
            }
        }
    };

    // ✅ TC002: Dummy test
    static TestCase register = new TestCase() {
        public String getName() {
            return "TC002 - Dummy Test";
        }

        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                driver.get(BASE_URL);
                driver.manage().window().maximize();
                System.out.println("Dummy test executed successfully!");
            } finally {
                driver.quit();
            }
        }
    };

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "./src/Driver/chromedriver");

        List<TestCase> tests = new ArrayList<>();
        tests.add(registerAccount);
        tests.add(register);

        // Run all test cases
        for (TestCase test : tests) {
            System.out.println("Running: " + test.getName());
            try {
                test.run();
                System.out.println(test.getName() + ": PASS");
            } catch (AssertionError ae) {
                System.out.println(test.getName() + ": FAIL -> " + ae.getMessage());
            } catch (Exception e) {
                System.out.println(test.getName() + ": ERROR");
                e.printStackTrace(System.out);
            }
            System.out.println("-------------------------------------");
        }
    }
}
