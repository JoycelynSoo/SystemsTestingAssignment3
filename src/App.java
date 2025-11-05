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
// import org.openqa.selenium.edge.EdgeDriver;
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
                driver.findElement(By.id("input-password")).sendKeys("SystemsTesting");
                driver.findElement(By.id("input-confirm")).sendKeys("SystemsTesting");
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
    static TestCase loginToAccount = new TestCase() {
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
 
                // Click on "Login" link
                WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Login']")));
                loginLink.click();
                wait.until(ExpectedConditions.titleContains("Account Login"));
                System.out.println("Navigated to Login page successfully!");
 
                // Fill in login form
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))
                        .sendKeys("Jane.Doe123@example.com");
                driver.findElement(By.id("input-password")).sendKeys("SystemsTesting");
 
                // Click Login button
                WebElement loginButton = driver.findElement(By.xpath("//input[@value='Login']"));
                loginButton.click();
 
                // Wait for successful login to the Account dashboard
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[normalize-space()='My Account']")));
 
                System.out.println("Login successful for Jane.Doe123@example.com");
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
                Thread.sleep(500);
                WebElement addBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.presenceOfElementLocated(addToCartLocator));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
 
            } finally {
                driver.quit();
            }
        }
    };

    // T004: Update quantity of an item in the cart 
    static TestCase updateCartQuantity = new TestCase() {
        public String getName() {
            return "T004 - Update quantity of an item in the cart ";
        }

        public void run() throws Exception {
            // WebDriver driver = new EdgeDriver();
            WebDriver driver = new ChromeDriver();
            try {
                // Go to the Software catalogue page
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get("https://ecommerce-playground.lambdatest.io/index.php?route=product/category&path=17");
                driver.manage().window().maximize();

                // Navigate to the 3rd page by clicking on the “3” page button
                WebElement pageThree = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@class,'page-link') and normalize-space(text())='3']")));
                pageThree.click();

                // Click on the item named “Palm Treo Pro”
                WebElement palmTreoProProduct = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Palm Treo Pro']")));
                palmTreoProProduct.click();
                Thread.sleep(3000);

                
                // Click the “Add to Cart” button to add the item to the cart
                WebElement container = driver.findElement(By.id("entry_216842"));
                WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                        container.findElement(By.cssSelector("button[class*='btn-cart']"))
                ));               
                addToCartButton.click();
                Thread.sleep(3000);

                // Wait for cart notification to pop up and click the "View Cart" button to navigate to the Shopping Cart page
                By findPopUp = By.cssSelector("div.toast.m-3.fade.show");
                WebElement popUpCartNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(findPopUp));
                WebElement popUpCartButton = popUpCartNotification.findElement(By.cssSelector("a.btn.btn-primary.btn-block"));
                wait.until(ExpectedConditions.elementToBeClickable(popUpCartButton));
                popUpCartButton.click();
                Thread.sleep(3000);

                // Clear existing quantity and input new quantity
                driver.findElement(By.cssSelector("input.form-control")).clear();
                driver.findElement(By.cssSelector("input.form-control")).sendKeys("4");

                // Click Update symbol to change item quantity
                driver.findElement(By.cssSelector("h1")).click(); 
                WebElement updateButton = driver.findElement(By.cssSelector("button[class='btn btn-primary']"));
                updateButton.click();
                Thread.sleep(3000);

                // Verify that the quantity has been successfully updated - will return fail if the "Success: You have modified your shopping cart!" message doesn't appear
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(text(),'You have modified your shopping cart!')]")
                ));
                Thread.sleep(3000);

            } finally {
                driver.quit();
            }
        }
    };

    // T005: Remove an item from the Cart
    static TestCase removeItemFromCart = new TestCase() {
        public String getName() {
            return "T005 - Remove an item from the Cart";
        }

        public void run() throws Exception {
            // WebDriver driver = new EdgeDriver();
            WebDriver driver = new ChromeDriver();
            try {
                // Go to the Software catalogue page
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get("https://ecommerce-playground.lambdatest.io/index.php?route=product/category&path=17");
                driver.manage().window().maximize();

                // Navigate to the 3rd page by clicking on the “3” page button
                WebElement pageThree = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@class,'page-link') and normalize-space(text())='3']")));
                pageThree.click();

                // Click on the item named “Palm Treo Pro”
                WebElement palmTreoProProduct = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Palm Treo Pro']")));
                palmTreoProProduct.click();
                Thread.sleep(3000);

                // Click the “Add to Cart” button to add the item to the cart
                WebElement container = driver.findElement(By.id("entry_216842"));
                WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                        container.findElement(By.cssSelector("button[class*='btn-cart']"))
                ));               
                addToCartButton.click();
                Thread.sleep(3000);

                // Wait for cart notification to pop up and click the "View Cart" button to navigate to the Shopping Cart page
                By findPopUp = By.cssSelector("div.toast.m-3.fade.show");
                WebElement popUpCartNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(findPopUp));
                WebElement popUpCartButton = popUpCartNotification.findElement(By.cssSelector("a.btn.btn-primary.btn-block"));
                wait.until(ExpectedConditions.elementToBeClickable(popUpCartButton));
                popUpCartButton.click();

                // Click the delete button to remove the item from the cart
                WebElement deleteButton = driver.findElement(By.cssSelector("button[class='btn btn-danger']"));
                deleteButton.click();

                // Verify that the item has been successfully removed from the cart - will return fail if the "Your shopping cart is empty" message doesn't appear
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//p[contains(text(),'Your shopping cart is empty')]")
                ));
                System.out.println("Item has been successfully removed from cart!");
                Thread.sleep(3000);

            } finally {
                driver.quit();
            }
        }
    };
 
    // T007 Product Comparison
    static TestCase productComparison = new TestCase() {
        public String getName() {
            return "TC007 - Product Comparison";
        }
 
        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get(BASE_URL);
                driver.manage().window().maximize();
 
                // Click “Shop by Category”
                WebElement shopByCategory = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(.,'Shop by Category')]")));
                shopByCategory.click();
 
                // Scroll and click "Desktop and Monitors"
                WebElement deskAndMon = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//span[normalize-space()='Desktops and Monitors']/ancestor::a")));
                Thread.sleep(500);
                deskAndMon.click();
                // Add “HTC Touch HD” to compare
                WebElement htcCompare = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[normalize-space()='HTC Touch HD']/ancestor::div[contains(@class,'product-thumb')]//button[contains(@class,'btn-compare')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", htcCompare);
 
                // Add “Palm Treo Pro” to compare
                WebElement palmCompare = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[normalize-space()='Palm Treo Pro']/ancestor::div[contains(@class,'product-thumb')]//button[contains(@class,'btn-compare')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", palmCompare);
 
            } finally {
                driver.quit();
            }
        }
    };
 
    // T008 Add Product to Wishlist
    static TestCase addProductWishList = new TestCase() {
        public String getName() {
            return "TC008 - Add Product to Wishlist";
        }
 
        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
                driver.get(BASE_URL);
                driver.manage().window().maximize();
 
                // Login first
                WebElement myAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[contains(@class,'dropdown-toggle') and .//span[contains(text(),'My account')]]")));
                Actions actions = new Actions(driver);
                actions.moveToElement(myAccount).perform();
 
                // Click on "Login" link
                WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Login']")));
                loginLink.click();
                wait.until(ExpectedConditions.titleContains("Account Login"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))
                        .sendKeys("Jane.Doe123@example.com");
                driver.findElement(By.id("input-password")).sendKeys("SystemsTesting");
                WebElement loginButton = driver.findElement(By.xpath("//input[@value='Login']"));
                loginButton.click();
 
                // Click “Shop by Category”
                WebElement shopByCategory = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(.,'Shop by Category')]")));
                shopByCategory.click();
 
                // Scroll and click “Cameras”
                WebElement cameraCategory = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//span[normalize-space()='Cameras']/ancestor::a")));
                Thread.sleep(500);
                cameraCategory.click();
 
                // Click on “Palm Treo Pro”
                WebElement palmTreo = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Palm Treo Pro']")));
                palmTreo.click();
 
                // On product page, click the heart icon to add to wishlist
                WebElement wishlistIcon = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//button[@data-original-title='Add to Wish List' or contains(@onclick,'wishlist.add')]")));
                wishlistIcon.click();
 
                // Click “Wish List (1)” button in the popup
                WebElement wishlistButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//a[contains(@href,'wishlist') and (contains(.,'Wish List') or contains(.,'(1)'))]")));
                wishlistButton.click();
 
            } finally {
                driver.quit();
            }
        }
    };
    // T009 Write product review
    static TestCase writeProductReview = new TestCase() {
        public String getName() {
            return "TC009 - Write product review";
        }
 
        public void run() throws Exception {
            WebDriver driver = new ChromeDriver();
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
 
                // Navigate to the homepage after login
                driver.get("https://ecommerce-playground.lambdatest.io/index.php?route=common/home");
 
                // Click “Shop by Category”
                WebElement shopByCategory = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(.,'Shop by Category')]")));
                shopByCategory.click();
 
                // Scroll and click “Phone, Tablets & iPod”
                WebElement phonesCategory = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//span[normalize-space()='Phone, Tablets & Ipod' or normalize-space()='Phone, Tablets & iPod']/ancestor::a")));
                phonesCategory.click();
 
                // Click on “iPod Touch”
                WebElement ipodTouch = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='iPod Touch']")));
                ipodTouch.click();
 
                // Write a revieww
                driver.findElement(By.id("input-name")).sendKeys("Jane Doe");
                driver.findElement(By.id("input-review")).sendKeys("The iPod Touch works well. Highly recommend!");
                WebElement fifthStarLabel = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("label[for^='rating-5-']")));
                fifthStarLabel.click();
 
                // Click “Write Review” button
                WebElement submitButton = driver.findElement(By.id("button-review"));
                submitButton.click();
 
            } finally {
                driver.quit();
            }
        }
    };
 
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "./src/Driver/chromedriver");
 
        List<TestCase> tests = new ArrayList<>();
        tests.add(registerAccount);
        tests.add(loginToAccount);
        tests.add(addItemToCart);
        tests.add(productComparison);
        tests.add(addProductWishList);
        tests.add(writeProductReview);
 
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
