//package jolt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EventGift {

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

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://jolt.film/");

        try {
            WebElement closeButton = driver.findElement(
                    By.cssSelector(".onetrust-close-btn-handler.banner-close-button.ot-close-icon"));
            closeButton.click();
            Thread.sleep(1000);
        } catch (Exception e) {}

        try {
            WebElement loginBtn = driver.findElement(By.cssSelector("button[aria-label='Log IN']"));
            loginBtn.click();
        } catch (Exception e) {}

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement usernameField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameField.sendKeys(un);

            WebElement continueBtn = driver.findElement(By.cssSelector("button._button-login-id"));
            continueBtn.click();

            List<WebElement> emailErrors = driver.findElements(By.xpath("//div[@id='error-cs-email-invalid']"));
            if (!emailErrors.isEmpty()) {
                System.out.println("⚠️ Validation: " + emailErrors.get(0).getText());
                driver.quit();
                return;
            }

            WebElement passwordField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordField.sendKeys(pw);

            WebElement loginPasswordBtn = driver.findElement(By.cssSelector("button._button-login-password"));
            loginPasswordBtn.click();
            Thread.sleep(1000);

            try {
                WebElement userNotExistMsg = wait1.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@class='cc6c34cee c0dad7210']")));
                System.out.println("⚠️ " + userNotExistMsg.getText());
                driver.quit();
                return;
            } catch (TimeoutException e) {}

            try {
                WebElement passwordError = wait1.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@id='error-element-password']")));
                System.out.println("⚠️ " + passwordError.getText());
                driver.quit();
                return;
            } catch (TimeoutException e) {}

            try {
                WebElement continueWithoutPasskey = wait1.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("button[value='abort-passkey-enrollment']")));
                continueWithoutPasskey.click();
                Thread.sleep(700);
            } catch (Exception e) {}

            System.out.println("Login Successful!");

        } catch (Exception e) {
            System.out.println("⚠️ Login error: " + e.getMessage());
        }

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

            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement targetMovie = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath(movieXpath)));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", targetMovie);
            Thread.sleep(800);

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetMovie);

            System.out.println("🎬 Movie clicked: " + movieName);

        } catch (Exception e) {
            System.out.println("❌ Movie not found or click failed: " + movieName);
            e.printStackTrace();
        }

        Thread.sleep(2000);

        List<WebElement> initialEvents = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));

        if (initialEvents.isEmpty()) {
            System.out.println("❌ No event available for this movie.");
            driver.quit();
            return;
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String EventSection = "//button[@aria-label='See all of your event registrations in My Orders.']";

        try {
            WebElement element = wait1.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(EventSection)));

            wait1.until(ExpectedConditions.visibilityOf(element));

            js.executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(1500);

            js.executeScript("window.scrollBy(0, -200);");
            Thread.sleep(1000);

            System.out.println("✅ Successfully scrolled to the Event section");

        } catch (Exception e) {}

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor jsEvt = (JavascriptExecutor) driver;

        List<String> allEventNames = new ArrayList<>();

        List<WebElement> events1 = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        for (WebElement ev : events1) {
            String n = ev.getText().trim();
            if (!n.isEmpty() && !allEventNames.contains(n)) {
                allEventNames.add(n);
            }
        }

        try {
            By rightarrowXpath = By.xpath(
                    "//div[@class='inner_container']//div[contains(@class,'upcoming_event_cards')]//div//div[@class='slick-arrow slick-next']//*[name()='svg']");

            try {
                for (int i = 0; i < 2; i++) {
                    WebElement rightArrow = wait.until(
                            ExpectedConditions.elementToBeClickable(rightarrowXpath)
                    );
                    rightArrow.click();
                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        } catch (Exception e1) {}

        List<WebElement> events2 = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        for (WebElement ev : events2) {
            String n = ev.getText().trim();
            if (!n.isEmpty() && !allEventNames.contains(n)) {
                allEventNames.add(n);
            }
        }

        List<WebElement> events3 = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        for (WebElement ev : events3) {
            String n = ev.getText().trim();
            if (!n.isEmpty() && !allEventNames.contains(n)) {
                allEventNames.add(n);
            }
        }

        driver.manage().window().minimize();

        System.out.println("------ AVAILABLE EVENTS (ALL) ------");
        for (int i = 0; i < allEventNames.size(); i++) {
            System.out.println((i + 1) + ". " + allEventNames.get(i));
        }

        int choice;
        String FEmail = "";
        String PN = "";
        String promoCode = "";

        while (true) {
            System.out.print("Select event from given above: ");
            choice = sc.nextInt();
            if (choice >= 1 && choice <= allEventNames.size()) break;

            System.out.println("⚠️ Please select from given list");
        }

        System.out.print("Enter the Friend's Email ID: ");
        sc.nextLine();
        FEmail = sc.nextLine();

        System.out.println("Do you want to Add Personal Note? (y/n): ");
        String ch = sc.next().trim().toLowerCase();
        if (ch.equals("y")) {
            System.out.println("Please Enter Personal Note : ");
            sc.nextLine();
            PN = sc.nextLine();
        }

        System.out.print("Enter the promo code: ");
        promoCode = sc.next();

        driver.manage().window().maximize();
        Thread.sleep(1000);

        List<WebElement> finalEvents = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        WebElement selectedEvent = finalEvents.get(choice - 1);

        By leftarrowXpath = By.xpath("//div[@class='slick-arrow slick-prev']//*[name()='svg']");

        if (choice != 4 && choice != 5) {
            try {
                for (int i = 0; i < 2; i++) {
                    WebElement leftArrow = wait.until(
                            ExpectedConditions.elementToBeClickable(leftarrowXpath)
                    );
                    leftArrow.click();
                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        }

        jsEvt.executeScript("arguments[0].click();", selectedEvent);

        boolean giftFound = false;
        try {
            WebElement GiftButton = wait1.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[@aria-label='Gift']")
                    )
            );
            GiftButton.click();
            System.out.println("✅ Clicked on Gift button");
            giftFound = true;
        } catch (TimeoutException e) {}

        if (!giftFound) {
            driver.quit();
            return;
        }
        Thread.sleep(800);

        try {
            WebElement FriendEmail = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@placeholder='email@example.com']")));
            FriendEmail.sendKeys(FEmail);
            System.out.println("✅ Freind's Email Entered");
        } catch (TimeoutException e) { }
        Thread.sleep(800);

        try {
            WebElement NextButton = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Next']")));
            NextButton.click();
            System.out.println("✅ Next Button Clicked");
        } catch (TimeoutException e) { }
        Thread.sleep(800);

        try {
            WebElement PersonalNote = wait1.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//label[contains(text(),'Personal note')]/following::input[1]")
                    )
            );

            // Only enter if user actually typed something
            if (!PN.trim().isEmpty()) {
                PersonalNote.sendKeys(PN);
                System.out.println("✅ Personal Note Entered");
            } else {
                
            }

        } catch (TimeoutException e) {
            System.out.println("ℹ️ Personal Note field not found");
        }

        clickIfAvailable(wait1, js, By.xpath("//button[@aria-label='Add to CART']"), "'ADD TO CART'");
        Thread.sleep(800);

        clickIfAvailable(wait1, js, By.cssSelector("button[aria-label='CHECKOUT']"), "'CHECKOUT'");
        Thread.sleep(800);

        try {
            WebElement promoInput = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='600']")));
            promoInput.clear();
            Thread.sleep(500);
            promoInput.sendKeys(promoCode);
            WebElement applyBtn = driver.findElement(By.xpath("//button[@aria-label='apply']"));
            applyBtn.click();
        } catch (TimeoutException e) { }

        List<WebElement> promoMsg = driver.findElements(By.xpath("//div[contains(text(),'Promo code applied successfully!')]"));

        if (!promoMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoMsg.get(0).getText());
        }

        List<WebElement> promoExpiredMsg = driver.findElements(
                By.xpath("//div[contains(text(),'Promo code is expired.')]")
        );

        if (!promoExpiredMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoExpiredMsg.get(0).getText());
        }

        List<WebElement> promoNotValidMsg = driver.findElements(
                By.xpath("//div[contains(text(),'Promo code is not valid or expired.')]")
        );

        if (!promoNotValidMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoNotValidMsg.get(0).getText());
        }

        Thread.sleep(800);

        clickIfAvailable(wait1, js, By.xpath("//button[@aria-label='PURCHASE']"), "'PURCHASE'");

        try {
            WebElement purchaseMsg = wait1.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(text(),'Order placed successfully')]")));

            System.out.println("Purchase Message: " + purchaseMsg.getText());
        } catch (TimeoutException e) { }

        driver.quit();
    }

    // 🔥 FIXED — FULLY WORKING CLICK METHOD
    private static void clickIfAvailable(WebDriverWait wait1, JavascriptExecutor js, By locator, String name) {
        try {
            WebElement element = wait1.until(ExpectedConditions.elementToBeClickable(locator));

            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
            Thread.sleep(500);

            js.executeScript("arguments[0].click();", element);
            System.out.println("✅ Clicked " + name);
            Thread.sleep(700);

        } catch (TimeoutException e) {
            System.out.println("⚠️ " + name + " button not available!");
        } catch (Exception e) {
            System.out.println("❌ Error clicking " + name + ": " + e.getMessage());
        }
    }
}
