package edu.pitt.cs;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.runners.MethodSorters;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class D3Test {
    private WebDriver driver;

    public D3Test() {}

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:8080");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.cookie = '1=false';document.cookie = '2=false';document.cookie = '3=false';");
    }

    @After
    public void tearDown() {
        if (this.driver != null)
            this.driver.quit();
    }

    @Test
    public void tEST1LINKS() {
        WebElement link = driver.findElement(By.linkText("Reset"));
        String hrefValue = link.getAttribute("href");
        // Reset points to /reset
        assertEquals("http://localhost:8080/reset", hrefValue);
    }

    @Test
    public void tEST2RESET() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.cookie = '1=true';document.cookie = '2=true';document.cookie = '3=true';");
        clickLink("Reset");

        WebElement cat1 = driver.findElement(By.xpath("//li[text()='ID 1. Jennyanydots']"));
        WebElement cat2 = driver.findElement(By.xpath("//li[text()='ID 2. Old Deuteronomy']"));
        WebElement cat3 = driver.findElement(By.xpath("//li[text()='ID 3. Mistoffelees']"));
        // Verify cat listings
        assertEquals("ID 1. Jennyanydots", cat1.getText());
        assertEquals("ID 2. Old Deuteronomy", cat2.getText());
        assertEquals("ID 3. Mistoffelees", cat3.getText());
    }

    @Test
    public void tEST3CATALOG() {
        clickLink("Catalog");
        WebElement image = driver.findElement(By.xpath("(//img)[2]"));
        // Verify second image is "cat2.jpg"
        assertEquals("http://localhost:8080/images/cat2.jpg", image.getAttribute("src"));
    }

    @Test
    public void tEST4LISTING() {
        clickLink("Catalog");

        List<WebElement> cats = driver.findElements(By.xpath("//div[@id='listing']//ul/li"));
        // Verify three cats
        assertEquals(3, cats.size());
        // Verify third is "ID 3. Mistoffelees"
        assertEquals("ID 3. Mistoffelees", cats.get(2).getText());
    }

    @Test
    public void tEST5RENTACAT() {
        driver.get("http://localhost:8080/rent-a-cat");

        // Verify "Rent" button
        WebElement rentButton = driver.findElement(By.xpath("//button[contains(text(), 'Rent')]"));
        assertNotNull("Rent button should exist on the page.", rentButton);
        // Verify "Return" button
        WebElement returnButton = driver.findElement(By.xpath("//button[contains(text(), 'Return')]"));
        assertNotNull("Return button should exist on the page.", returnButton);
    }

    @Test
    public void tEST6RENT() {
        clickLink("Rent-A-Cat");

        // Rent cat 1
        WebElement rentInput = driver.findElement(By.xpath("//*[@id='rentID']"));
        rentInput.sendKeys("1");
        WebElement rentButton = driver.findElement(By.xpath("//button[@onclick='rentSubmit()']"));
        rentButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rentResult")));
        
        // Verify cat 1 is "Rented out"
        List<WebElement> catListings = driver.findElements(By.xpath("//main//ul/li"));
        assertEquals("Rented out", catListings.get(0).getText());
        // Verify cat 2 is "ID 2. Old Deuteronomy"
        assertEquals("ID 2. Old Deuteronomy", catListings.get(1).getText());
        // Verify cat 3 is "ID 3. Mistoffelees"
        assertEquals("ID 3. Mistoffelees", catListings.get(2).getText());
        // Verify "Success!" is in result element
        WebElement rentResult = driver.findElement(By.id("rentResult"));
        assertEquals("Success!", rentResult.getText());
    }
    

    @Test
    public void tEST7RETURN() {
        clickLink("Rent-A-Cat");

        // Rent cat 2
        WebElement rentInput = driver.findElement(By.id("rentID"));
        rentInput.sendKeys("2");
        WebElement rentButton = driver.findElement(By.xpath("//button[@onclick='rentSubmit()']"));
        rentButton.click();

        // Wait for the result text to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement rentResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rentResult")));

        // Verify success message
        assertEquals("Success!", rentResult.getText());
        List<WebElement> catListings = driver.findElements(By.xpath("//div[@id='listing']//ul/li"));
        // Verify cat 1
        assertEquals("ID 1. Jennyanydots", catListings.get(0).getText());
        // Verify cat 2
        assertEquals("Rented out", catListings.get(1).getText());
        // Verify cat 3
        assertEquals("ID 3. Mistoffelees", catListings.get(2).getText());
    }

    @Test
    public void tEST8FEEDACAT() {
        clickLink("Feed-A-Cat");

        WebElement feedButton = driver.findElement(By.xpath("//button[@onclick='setTimeout(feedSubmit, 1000)']"));
        assertNotNull("Feed button should exist on the page.", feedButton);
        // Verify that button is displayed 
        assertTrue("Feed button should be visible.", feedButton.isDisplayed());
    }

    @Test
    public void tEST9FEED() {
        clickLink("Feed-A-Cat");

        WebElement catnipInput = driver.findElement(By.id("catnips"));
        catnipInput.sendKeys("6"); // Feed 6 catnips
        WebElement feedButton = driver.findElement(By.xpath("//button[@onclick='setTimeout(feedSubmit, 1000)']"));
        feedButton.click();

        // Wait for feed result
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement feedResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("feedResult")));
        wait.until(ExpectedConditions.textToBe(By.id("feedResult"), "Nom, nom, nom."));

        // Verify that "Nom, nom, nom." is displayed
        assertEquals("Nom, nom, nom.", feedResult.getText());
    }

    @Test
    public void tEST10GREETACAT() {
        clickLink("Greet-A-Cat");

        WebElement greetingText = driver.findElement(By.xpath("//*[contains(text(), 'Meow!Meow!Meow!')]"));
        // Verify "Meow!Meow!Meow!" shows
        assertTrue("The greeting text should be displayed on the page.", greetingText.isDisplayed());
    }

    @Test
    public void tEST11GREETACATWITHNAME() {
        driver.get("http://localhost:8080/greet-a-cat/Jennyanydots");

        WebElement greetingText = driver.findElement(By.xpath("//*[contains(text(), 'Meow! from Jennyanydots.')]"));
        // Verify "Meow! from Jennyanydots." shows
        assertTrue("The greeting text should be displayed on the page.", greetingText.isDisplayed());
    }
    
    private void clickLink(String link) {
        WebElement catalogLink = driver.findElement(By.linkText(link));
        catalogLink.click();
    }
}
