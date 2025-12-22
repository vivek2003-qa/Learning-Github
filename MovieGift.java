package jolt;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class MovieGift {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ----------- Sign In First -----------
        System.out.print("Please Enter Email Address: ");
        String un = sc.next();
        System.out.print("Please Enter Password: ");
        String pw = sc.next();
        sc.nextLine();
        System.out.print("Enter the movie name: ");
        String movieName = sc.nextLine();
        System.out.print("Enter the Friend's Email ID: ");
        String FEmail = sc.nextLine(); 
        // Personal Note
        System.out.println("Do you want to Add Personal Note? (y/n): ");
        String choice = sc.next().trim().toLowerCase();
        String PN = "";
        if (choice.equals("y")) {
            System.out.println("Please Enter Personal Note : ");
            sc.nextLine();        
            PN = sc.nextLine();   
        }

        System.out.print("Enter the promo code: ");
         
        String promoCode = sc.nextLine();

        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://www.jolt.film");

        // Close cookie popup if present
        try {
            WebElement closeButton = driver.findElement(
                By.cssSelector(".onetrust-close-btn-handler.banner-close-button.ot-close-icon"));
            closeButton.click();
            System.out.println("✅ Cookie popup closed.");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("✅ Cookie popup not found or already closed.");
        }

        // Click Login button
        try {
            WebElement loginBtn = driver.findElement(By.cssSelector("button[aria-label='Log IN']"));
            loginBtn.click();
            System.out.println("✅ Log IN button Clicked.");
        } catch (Exception e) {
            System.out.println("Login button not found: " + e.getMessage());
        }

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Enter email and click Continue
            WebElement usernameField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameField.sendKeys(un);

            WebElement continueBtn = driver.findElement(By.cssSelector("button._button-login-id"));
            continueBtn.click();
            System.out.println("✅ Click on Continue after Email.");

            // ✅ Email validation using if-else instead of try-catch
            java.util.List<WebElement> emailErrors = driver.findElements(By.xpath("//div[@id='error-cs-email-invalid']"));
            if (!emailErrors.isEmpty()) {
                String validationMsg = emailErrors.get(0).getText();
                System.out.println("⚠️ Validation Message: " + validationMsg);
                driver.quit();
                sc.close();
                return;
            } 
            else {
            }

            // Continue to password only if no validation message shown
            WebElement passwordField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordField.sendKeys(pw);            

            WebElement loginPasswordBtn = driver.findElement(By.cssSelector("button._button-login-password"));
            loginPasswordBtn.click();
            System.out.println("✅ Clicked on Continue after Password.");
            
            // ✅ Wait briefly for possible validation messages
            Thread.sleep(1000);

            // ---- Check: User does not exist ----
            try {
                WebElement userNotExistMsg = wait1.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@class='cc6c34cee c0dad7210']")));
                System.out.println("⚠️ Validation Message: " + userNotExistMsg.getText());
                driver.quit();
                sc.close();
                return;
            } catch (TimeoutException e) {
                
            }

            // ---- Check: Password error ----
            try {
                WebElement passwordError = wait1.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//span[@id='error-element-password']")));
                System.out.println("⚠️ Validation Message: " + passwordError.getText());
                driver.quit();
                sc.close();
                return;
            } catch (TimeoutException e) {
               
            }
            
            // "continue without passkey"
            try {
            	WebElement continueWithoutPasskey = wait1.until(
            		    ExpectedConditions.visibilityOfElementLocated(
            		        By.cssSelector("button[value='abort-passkey-enrollment']")));
            		continueWithoutPasskey.click();
            		System.out.println("✅ Clicked on Continue without passkey.");
            		Thread.sleep(500);
            } catch (Exception e) {
                
            }
            System.out.println("✅ Login Successful!");
        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
        }

        // --- Movie Selection (fixed) ---
        try {
            String movieLower = movieName.toLowerCase();
            String xpathLiteral;
            if (!movieLower.contains("'")) {
                xpathLiteral = "'" + movieLower + "'";
            } else if (!movieLower.contains("\"")) {
                xpathLiteral = "\"" + movieLower + "\"";
            } else {
                String[] parts = movieLower.split("'");
                StringBuilder sb = new StringBuilder("concat(");
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) sb.append(", \"'\", ");
                    sb.append("'").append(parts[i]).append("'");
                }
                sb.append(")");
                xpathLiteral = sb.toString();
            }

            String movieXpath =
            	    "//div[contains(@class,'movie_card')]//*[self::img or self::h3 or self::p or self::span]" +
            	    "[contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), " + xpathLiteral + ")" +
            	    " or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), " + xpathLiteral + ")]";


            java.util.List<WebElement> matches = driver.findElements(By.xpath(movieXpath));

            if (matches.isEmpty()) {
                System.out.println("❌ Movie not found: " + movieName);
            } else {
                WebElement target = matches.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", target);
                Thread.sleep(800);

                try {
                    target.click();
                    System.out.println("✅ Clicked on movie : " + movieName);
                    
                } catch (Exception e1) {
                    try {
                        WebElement ancestor = target.findElement(By.xpath("./ancestor::a[1]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", ancestor);
                        //Thread.sleep(400);
                        ancestor.click();
                        System.out.println("✅ Clicked ancestor link for movie: " + movieName);
                    } catch (Exception e2) {
                        ((JavascriptExecutor) driver).executeScript(
                            "var evt = new MouseEvent('click', {bubbles:true, cancelable:true, view:window}); arguments[0].dispatchEvent(evt);",
                            target
                        );
                        System.out.println("✅ Clicked movie via JS event dispatch (fallback).");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Movie selection error: " + e.getMessage());
        }
      
       
 
        // --- check not available in your region using XPath + JS Alert ---
        try {
            // First check via XPath
            List<WebElement> regionPopup = driver.findElements(
                By.xpath("//div[contains(text(),'This film is not available in your region.')]")
            );

            if (!regionPopup.isEmpty()) {
                // If popup found → trigger JavaScript alert
                js.executeScript("alert('REGION_BLOCKED');");
            }

            // Now wait max 2 sec for JS alert
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            alertWait.until(ExpectedConditions.alertIsPresent());

            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            if (alertText.equals("REGION_BLOCKED")) {
                System.out.println("✅ Region Popup Detected via Alert!");
            } else {
                System.out.println("⚠️ Unexpected alert text: " + alertText);
            }

            alert.accept(); // close alert

        } catch (TimeoutException e) {
            System.out.println("⚠️ Region restriction popup NOT detected (no alert).");
        } catch (Exception e) {
            System.out.println("❌ Error detecting region popup through alert: " + e.getMessage());
        }
        Thread.sleep(800);
        
        // --- Gift Button ---
        boolean giftFound = false;
        try {
            WebElement GiftButton = wait1.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'flex items-center gap-[1rem] cursor-pointer max-ml:hidden')]//*[name()='svg']")
                )
            );
            GiftButton.click();
            System.out.println("✅ Clicked on Gift button");
            giftFound = true;
        } catch (TimeoutException e) {
           
        }

        // If gift button is not available → STOP further gift steps
        if (!giftFound) {
            driver.quit();
            return;
        }
        Thread.sleep(800);
        
        // --- Friend Email ID ---
        try {
            WebElement FriendEmail = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@placeholder='email@example.com']")));
            FriendEmail.sendKeys(FEmail);
            System.out.println("✅ Freind's Email Entered");
        } catch (TimeoutException e) {   
        }
        Thread.sleep(800);
        
        // --- Next Button ---
        try {
            WebElement NextButton = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Next']")));
            NextButton.click();
            System.out.println("✅ Next Button Clicked");
        } catch (TimeoutException e) {   
        }
        Thread.sleep(800);
        
     // --- Personal Note ---
        try {
            WebElement PersonalNote = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[contains(text(),'Personal note')]/following::input[1]")));
            PersonalNote.sendKeys(PN);
            System.out.println("✅ Personal Note Entered");
        } catch (TimeoutException e) {
          
        }
        Thread.sleep(800);

        
        // --- Add to Cart ---
        
		clickIfAvailable(wait1, js, By.xpath("//button[@aria-label='Add to CART']"), "'ADD TO CART'");
		Thread.sleep(800);
        // --- Checkout ---
        clickIfAvailable(wait1, js, By.cssSelector("button[aria-label='CHECKOUT']"), "'CHECKOUT'");
       
        Thread.sleep(800);
        // --- Apply Promo Code ---
        try {
            WebElement promoInput = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='600']")));
            promoInput.clear();
            Thread.sleep(500);
            promoInput.sendKeys(promoCode);
            WebElement applyBtn = driver.findElement(By.xpath("//button[@aria-label='apply']"));
            applyBtn.click();
            
            
        } catch (TimeoutException e) {
            
        }
        
        // ---- Promocode success message ----
        List<WebElement> promoMsg = driver.findElements(By.xpath("//div[contains(text(),'Promo code applied successfully!')]"));

        if (!promoMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoMsg.get(0).getText());
        } else {
           
        }
            
        
        // ---- Check for promo code expired or not ----
        List<WebElement> promoExpiredMsg = driver.findElements(
            By.xpath("//div[contains(text(),'Promo code is expired.')]")
        );

        if (!promoExpiredMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoExpiredMsg.get(0).getText());
        } else {
           
        }

        
        // ---- Check for promo code not valid ----
        List<WebElement> promoNotValidMsg = driver.findElements(
            By.xpath("//div[contains(text(),'Promo code is not valid or expired.')]")
        );

        if (!promoNotValidMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoNotValidMsg.get(0).getText());
        } else {
           
        }
        Thread.sleep(800);
        // --- Purchase ---
        clickIfAvailable(wait1, js, By.xpath("//button[@aria-label='PURCHASE']"), "'PURCHASE'");
        
        // ---- Purchase success message ----
        try {
            WebElement purchaseMsg = wait1.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(),'Order placed successfully')]")));
            
            System.out.println("Purchase Message: " + purchaseMsg.getText());
        } catch (TimeoutException e) {
        }

   
        driver.quit();
    }

    // ----------- Helper Method to Click Buttons Safely -----------
    private static void clickIfAvailable(WebDriverWait wait, JavascriptExecutor js, By locator, String name) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
            Thread.sleep(500);

            try {
                element.click();
            } catch (Exception e1) {
                try {
                    new org.openqa.selenium.interactions.Actions((WebDriver) js)
                            .moveToElement(element)
                            .click()
                            .perform();
                } catch (Exception e2) {
                    js.executeScript(
                            "var evt = new MouseEvent('click', {bubbles:true, cancelable:true, view:window}); arguments[0].dispatchEvent(evt);",
                            element
                    );
                }
            }

            System.out.println("✅ " + name + " button clicked.");
            Thread.sleep(1000);

        } catch (TimeoutException e) {
            System.out.println("⚠️ " + name + " button not available.");
        } catch (Exception e) {
            System.out.println("❌ Failed to click " + name + " button: " + e.getMessage());
        }
    }
    }
