/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.paolobd.gamegui;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class LibraryTest {
    private WebDriver driver;
    private MainPage mainPage;

    @BeforeEach        public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Fix the issue https://github.com/SeleniumHQ/selenium/issues/11750
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver = GameGui.gamifyWebDriver(driver);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://www.jetbrains.com/");

        mainPage = new MainPage(driver);
    }

    @AfterEach        public void tearDown() {
        driver.quit();
    }

    @Test
    public void search() {
        mainPage.searchButton.click();

        WebElement searchField = driver.findElement(By.cssSelector("[data-test='search-input']"));
        searchField.sendKeys("Selenium");

        WebElement submitButton = driver.findElement(By.cssSelector("button[data-test='full-search-button']"));
        submitButton.click();

        WebElement searchPageField = driver.findElement(By.cssSelector("input[data-test='search-input']"));

        searchPageField.click();

        assertEquals("Selenium", searchPageField.getAttribute("value"));            }

    @Test
    public void toolsMenu() {
        mainPage.toolsMenu.click();

        WebElement menuPopup = driver.findElement(By.cssSelector("div[data-test='main-submenu']"));
        assertTrue(menuPopup.isDisplayed());
    }

    @Test
    public void navigationToAllTools() {
        mainPage.seeDeveloperToolsButton.click  ();
        mainPage.findYourToolsButton.click();

        WebElement productsList = driver.findElement(By.id("products-page"));
        assertTrue(productsList.isDisplayed());
        assertEquals("All Developer Tools and Products by JetBrains", driver.getTitle());            }

    @Test
    public void navigateByClicking() {
        mainPage.linkForDevelopers.click();

        assertEquals("https://www.jetbrains.com/products/#type=ide-vs",driver.getCurrentUrl());
    }

    @Test
    public void loginTest() {
        driver.get("https://idp.polito.it/idp/x509mixed-login");
        WebElement userName = driver.findElement(By.cssSelector("[id$='username']"));
        WebElement password = driver.findElement(By.cssSelector("input[id$='password']"));

        userName.sendKeys("s292479");
        password.sendKeys("Stefanut98!");

        password.submit();
    }
}