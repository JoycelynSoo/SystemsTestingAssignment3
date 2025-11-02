import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
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

    // T001: Register a new account
    static TestCase registerAccount = new TestCase() {
        public String getName() {
            return "TC001 - Register Account";
        }

        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                // Navigate to home page
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get(BASE_URL);
                driver.manage().window().maximize();

                // Hover over "My account"
                WebElement myAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[contains(@class,'dropdown-toggle') and .//span[contains(text(),'My account')]]")));

                Actions actions = new Actions(driver);
                actions.moveToElement(myAccount).perform();

                // Click on "Register" link
                WebElement registerLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Register']")));
                registerLink.click();
                wait.until(ExpectedConditions.titleContains("Register Account"));
                System.out.println("Navigated to Register page successfully!");

                // Fill in registration form
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname"))).sendKeys("Jane");
                driver.findElement(By.id("input-lastname")).sendKeys("Doe");
                driver.findElement(By.id("input-email")).sendKeys("Jane.Doe123@example.com");
                driver.findElement(By.id("input-telephone")).sendKeys("0400000000");
                driver.findElement(By.id("input-password")).sendKeys("1234");
                driver.findElement(By.id("input-confirm")).sendKeys("1234");
                WebElement subscribeNo = driver.findElement(By.xpath("//input[@name='newsletter' and @value='0']"));
                if (!subscribeNo.isSelected())
                    subscribeNo.click();
                WebElement privacyLabel = driver.findElement(By.cssSelector("label[for='input-agree']"));
                privacyLabel.click();

                // Click Continue
                driver.findElement(By.xpath("//input[@value='Continue']")).click();

                // Confirm success
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h1[normalize-space()='Your Account Has Been Created!']")));
                System.out.println("Registration successful for Jane Doe");

            } finally {
                driver.quit();
            }
        }
    };

    // T002: User Login
    static TestCase register = new TestCase() {
        public String getName() {
            return "TC002 - User Login";
        }

        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                // Navigate to home page
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get(BASE_URL);
                driver.manage().window().maximize();

                // Hover over "My account"
                WebElement myAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[contains(@class,'dropdown-toggle') and .//span[contains(text(),'My account')]]")));
                Actions actions = new Actions(driver);
                actions.moveToElement(myAccount).perform();

                // Click on "Register" link
                WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Login']")));
                loginLink.click();
                wait.until(ExpectedConditions.titleContains("Account Login"));
                System.out.println("Navigated to Login page successfully!");

                // --- Fill in login form ---
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))
                        .sendKeys("Jane.Doe123@example.com");
                driver.findElement(By.id("input-password")).sendKeys("1234");

                // Click Login button
                WebElement loginButton = driver.findElement(By.xpath("//input[@value='Login']"));
                loginButton.click();

                // Wait for successful login to the Account dashboard
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[normalize-space()='My Account']")));

                System.out.println("Login successful for Jane.Doe000@example.com");
            } finally {
                driver.quit();
            }
        }
    };

    // T003 Add an item to the cart
    static TestCase addItemToCart = new TestCase() {
        public String getName() {
            return "TC003 - Add an item to the cart";
        }

        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                // Navigate to home page
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get("https://ecommerce-playground.lambdatest.io/index.php?route=product/category&path=25");
                driver.manage().window().maximize();

                // Click on “Apple Cinema 30”
                WebElement appleCinema = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Apple Cinema 30\"']")));
                appleCinema.click();

                // Click on “Please select” dropdown
                WebElement selectDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("input-option231-216836")));
                selectDropdown.click();

                // Select “Medium” size
                WebElement mediumOption = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//option[normalize-space()='Medium (-$28.80)']")));
                mediumOption.click();
                driver.findElement(By.cssSelector("h1")).click();

                // Add item to cart
                By addToCartLocator = By.xpath("//button[contains(@class,'btn-cart') and @title='Add to Cart']");
                WebElement addBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.presenceOfElementLocated(addToCartLocator));
                addBtn.click();

                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".alert-success")));
                System.out.println("Apple Cinema 30 (Medium) added to cart successfully!");

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
        tests.add(addItemToCart);

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
