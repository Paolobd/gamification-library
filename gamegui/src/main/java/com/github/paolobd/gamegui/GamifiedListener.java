package com.github.paolobd.gamegui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class GamifiedListener implements WebDriverListener {
    private final WebDriver driver;
    private final List<Event> eventList = new ArrayList<>();
    private String currentUrl = "";
    private Boolean passwordInserted = false;
    private final String[] lastClickedElement = new String[2];

    public GamifiedListener(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void afterAnyCall(Object target, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyCall(target, method, args, result);
    }

    @Override
    public void afterAnyWebDriverCall(WebDriver driver, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyWebDriverCall(driver, method, args, result);
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        WebDriverListener.super.afterGet(driver, url);

        String newUrl = extractUrl(url);
        if (!Objects.equals(newUrl, currentUrl)) {
            currentUrl = newUrl;
            addEvent(new Event("", currentUrl, EventType.NAVIGATION));
        }

    }

    @Override
    public void afterGetCurrentUrl(String result, WebDriver driver) {
        WebDriverListener.super.afterGetCurrentUrl(result, driver);
    }

    @Override
    public void afterGetTitle(WebDriver driver, String result) {
        WebDriverListener.super.afterGetTitle(driver, result);
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        WebDriverListener.super.beforeFindElement(driver, locator);
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        WebDriverListener.super.afterFindElement(driver, locator, result);

        if (result == null) return;

        String reference = locator.toString().split(":")[1].trim();

        if (locator instanceof By.ById) {
            addEvent(new Event(reference, currentUrl, EventType.LOCATOR_ID));
        } else if (locator instanceof By.ByCssSelector) {
            addEvent(new Event(reference, currentUrl, EventType.LOCATOR_CSS));
        } else if (locator instanceof By.ByXPath) {
            addEvent(new Event(reference, currentUrl, EventType.LOCATOR_XPATH));
        } else {
            addEvent(new Event(reference, currentUrl, EventType.LOCATOR));
        }
    }

    @Override
    public void afterFindElements(WebDriver driver, By locator, List<WebElement> result) {
        WebDriverListener.super.afterFindElements(driver, locator, result);
    }

    @Override
    public void afterGetPageSource(WebDriver driver, String result) {
        WebDriverListener.super.afterGetPageSource(driver, result);
    }

    @Override
    public void afterClose(WebDriver driver) {
        WebDriverListener.super.afterClose(driver);
    }

    @Override
    public void beforeQuit(WebDriver driver) {
        WebDriverListener.super.beforeQuit(driver);
        System.out.println(eventList);

        /*try {
            sendData();
        } catch (IOException | URISyntaxException e) {
            Logger logger = Logger.getLogger("");
            logger.warning("Could not communicate with the plugin. Probably you're using this library without" +
                    "the Intellij Gamification Plugin");
        }*/
    }

    @Override
    public void afterQuit(WebDriver driver) {
        WebDriverListener.super.afterQuit(driver);
    }

    @Override
    public void afterGetWindowHandles(WebDriver driver, Set<String> result) {
        WebDriverListener.super.afterGetWindowHandles(driver, result);
    }

    @Override
    public void afterGetWindowHandle(WebDriver driver, String result) {
        WebDriverListener.super.afterGetWindowHandle(driver, result);
    }

    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        WebDriverListener.super.afterExecuteScript(driver, script, args, result);
    }

    @Override
    public void afterExecuteAsyncScript(WebDriver driver, String script, Object[] args, Object result) {
        WebDriverListener.super.afterExecuteAsyncScript(driver, script, args, result);
    }

    @Override
    public void afterPerform(WebDriver driver, Collection<Sequence> actions) {
        WebDriverListener.super.afterPerform(driver, actions);
    }

    @Override
    public void afterResetInputState(WebDriver driver) {
        WebDriverListener.super.afterResetInputState(driver);
    }

    @Override
    public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyWebElementCall(element, method, args, result);
    }

    @Override
    public void beforeClick(WebElement element) {
        WebDriverListener.super.beforeClick(element);

        if (element == null) return;

        /*String reference;

        String id = element.getAttribute("id");
        String name = element.getAttribute("name");

        System.out.println("Id: " + element.getAttribute("id"));
        System.out.println("Text: " + element.getText());
        System.out.println("Tag Name: " + element.getTagName());
        System.out.println("Name: " + element.getAttribute("name"));
        System.out.println("Class: " + element.getAttribute("class"));
        System.out.println("Type: " + element.getAttribute("type"));
        System.out.println("Style: " + element.getAttribute("style"));
        System.out.println("Value: " + element.getAttribute("value"));
        System.out.println("Href: " + element.getAttribute("href"));
        System.out.println("Title: " + element.getAttribute("title"));
        System.out.println("Location: " + element.getLocation());

         if(Objects.equals(id, "") || id == null){
             if(Objects.equals(name, "") || name == null){
                 reference = element.getText().trim();
             }
             else {
                reference = name;
             }
         }
         else {
             reference = id;
         }*/

        lastClickedElement[0] = createWebElementId(element);
        lastClickedElement[1] = element.getAttribute("type");

    }

    @Override
    public void afterClick(WebElement element) {
        WebDriverListener.super.afterClick(element);

        if (element == null) return;

        String type = lastClickedElement[1];

        String newUrl = extractUrl(driver.getCurrentUrl());

        /*if(Objects.equals(type, "button")){
            Event event = new Event(lastClickedElement[0], newUrl, EventType.CLICK);
            addEvent(event);
        }*/

        if (Objects.equals(type, "submit")) {
            addEvent(new Event(lastClickedElement[0], currentUrl, EventType.CLICK));
            addEvent(new Event(lastClickedElement[0], currentUrl, EventType.SUBMIT));
        } else {
            addEvent(new Event(lastClickedElement[0], currentUrl, EventType.CLICK));
        }

        if (passwordInserted) {
            passwordInserted = false;
            addEvent(new Event(lastClickedElement[0], currentUrl, EventType.LOGIN));
        }

        if (!Objects.equals(currentUrl, newUrl)) {
            currentUrl = newUrl;
            addEvent(new Event("", currentUrl, EventType.NAVIGATION));
        }

    }

    @Override
    public void beforeSubmit(WebElement element) {
        WebDriverListener.super.beforeSubmit(element);

        if (element == null) return;

        lastClickedElement[0] = createWebElementId(element);
        lastClickedElement[1] = element.getAttribute("type");
    }

    @Override
    public void afterSubmit(WebElement element) {
        WebDriverListener.super.afterSubmit(element);

        String newUrl = extractUrl(driver.getCurrentUrl());

        addEvent(new Event(lastClickedElement[0], currentUrl, EventType.SUBMIT));

        if (passwordInserted) {
            passwordInserted = false;
            addEvent(new Event(lastClickedElement[0], currentUrl, EventType.LOGIN));
        }

        if (!Objects.equals(currentUrl, newUrl)) {
            currentUrl = newUrl;
            addEvent(new Event("", currentUrl, EventType.NAVIGATION));
        }

    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        WebDriverListener.super.afterSendKeys(element, keysToSend);

        String id = createWebElementId(element);
        addEvent(new Event(id, currentUrl, EventType.SEND_KEYS));

        String type = element.getAttribute("type");

        if (Objects.equals(type, "password")) {
            passwordInserted = true;
        }
    }

    @Override
    public void afterClear(WebElement element) {
        WebDriverListener.super.afterClear(element);
    }

    @Override
    public void afterGetTagName(WebElement element, String result) {
        WebDriverListener.super.afterGetTagName(element, result);
    }

    @Override
    public void afterGetAttribute(WebElement element, String name, String result) {
        WebDriverListener.super.afterGetAttribute(element, name, result);
    }

    @Override
    public void afterIsSelected(WebElement element, boolean result) {
        WebDriverListener.super.afterIsSelected(element, result);

        EventType type;
        if (result) {
            type = EventType.SELECTED_TRUE;
        } else {
            type = EventType.SELECTED_FALSE;
        }

        addEvent(new Event(createWebElementId(element), currentUrl, type));
    }

    @Override
    public void afterIsEnabled(WebElement element, boolean result) {
        WebDriverListener.super.afterIsEnabled(element, result);

        EventType type;
        if (result) {
            type = EventType.ENABLED_TRUE;
        } else {
            type = EventType.ENABLED_FALSE;
        }

        addEvent(new Event(createWebElementId(element), currentUrl, type));
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        WebDriverListener.super.afterGetText(element, result);
    }


    @Override
    public void afterFindElement(WebElement element, By locator, WebElement result) {
        WebDriverListener.super.afterFindElement(element, locator, result);
    }

    @Override
    public void afterFindElements(WebElement element, By locator, List<WebElement> result) {
        WebDriverListener.super.afterFindElements(element, locator, result);
    }

    @Override
    public void afterIsDisplayed(WebElement element, boolean result) {
        WebDriverListener.super.afterIsDisplayed(element, result);

        EventType type;
        if (result) {
            type = EventType.DISPLAYED_TRUE;
        } else {
            type = EventType.DISPLAYED_FALSE;
        }

        addEvent(new Event(createWebElementId(element), currentUrl, type));
    }

    @Override
    public void afterGetLocation(WebElement element, Point result) {
        WebDriverListener.super.afterGetLocation(element, result);
    }

    @Override
    public void afterGetSize(WebElement element, Dimension result) {
        WebDriverListener.super.afterGetSize(element, result);
    }

    @Override
    public void afterGetCssValue(WebElement element, String propertyName, String result) {
        WebDriverListener.super.afterGetCssValue(element, propertyName, result);
    }

    @Override
    public void afterAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyNavigationCall(navigation, method, args, result);

        String newUrl = extractUrl(driver.getCurrentUrl());

        if (!Objects.equals(newUrl, currentUrl)) {
            addEvent(new Event("", newUrl, EventType.NAVIGATION));
            currentUrl = newUrl;
        }
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        WebDriverListener.super.afterTo(navigation, url);
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, URL url) {
        WebDriverListener.super.afterTo(navigation, url);
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        WebDriverListener.super.afterBack(navigation);
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        WebDriverListener.super.afterForward(navigation);
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        WebDriverListener.super.afterRefresh(navigation);
    }

    @Override
    public void afterAnyAlertCall(Alert alert, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyAlertCall(alert, method, args, result);
    }

    @Override
    public void afterAccept(Alert alert) {
        WebDriverListener.super.afterAccept(alert);
    }

    @Override
    public void afterDismiss(Alert alert) {
        WebDriverListener.super.afterDismiss(alert);
    }

    @Override
    public void afterGetText(Alert alert, String result) {
        WebDriverListener.super.afterGetText(alert, result);
    }

    @Override
    public void afterSendKeys(Alert alert, String text) {
        WebDriverListener.super.afterSendKeys(alert, text);
    }

    @Override
    public void afterAnyOptionsCall(WebDriver.Options options, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyOptionsCall(options, method, args, result);
    }

    @Override
    public void afterAddCookie(WebDriver.Options options, Cookie cookie) {
        WebDriverListener.super.afterAddCookie(options, cookie);
    }

    @Override
    public void afterDeleteCookieNamed(WebDriver.Options options, String name) {
        WebDriverListener.super.afterDeleteCookieNamed(options, name);
    }

    @Override
    public void afterDeleteCookie(WebDriver.Options options, Cookie cookie) {
        WebDriverListener.super.afterDeleteCookie(options, cookie);
    }

    @Override
    public void afterDeleteAllCookies(WebDriver.Options options) {
        WebDriverListener.super.afterDeleteAllCookies(options);
    }

    @Override
    public void afterGetCookies(WebDriver.Options options, Set<Cookie> result) {
        WebDriverListener.super.afterGetCookies(options, result);
    }

    @Override
    public void afterGetCookieNamed(WebDriver.Options options, String name, Cookie result) {
        WebDriverListener.super.afterGetCookieNamed(options, name, result);
    }

    @Override
    public void afterAnyTimeoutsCall(WebDriver.Timeouts timeouts, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyTimeoutsCall(timeouts, method, args, result);
    }

    @Override
    public void afterImplicitlyWait(WebDriver.Timeouts timeouts, Duration duration) {
        WebDriverListener.super.afterImplicitlyWait(timeouts, duration);

        addEvent(new Event(timeouts.toString(), currentUrl, EventType.WAIT));
    }

    @Override
    public void afterSetScriptTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        WebDriverListener.super.afterSetScriptTimeout(timeouts, duration);
    }

    @Override
    public void afterPageLoadTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        WebDriverListener.super.afterPageLoadTimeout(timeouts, duration);
    }

    @Override
    public void afterAnyWindowCall(WebDriver.Window window, Method method, Object[] args, Object result) {
        WebDriverListener.super.afterAnyWindowCall(window, method, args, result);
    }

    @Override
    public void afterGetSize(WebDriver.Window window, Dimension result) {
        WebDriverListener.super.afterGetSize(window, result);
    }

    @Override
    public void afterSetSize(WebDriver.Window window, Dimension size) {
        WebDriverListener.super.afterSetSize(window, size);
    }

    @Override
    public void afterGetPosition(WebDriver.Window window, Point result) {
        WebDriverListener.super.afterGetPosition(window, result);
    }

    @Override
    public void afterSetPosition(WebDriver.Window window, Point position) {
        WebDriverListener.super.afterSetPosition(window, position);
    }

    @Override
    public void afterMaximize(WebDriver.Window window) {
        WebDriverListener.super.afterMaximize(window);
    }

    @Override
    public void afterFullscreen(WebDriver.Window window) {
        WebDriverListener.super.afterFullscreen(window);
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        WebDriverListener.super.onError(target, method, args, e);
    }

    private String extractUrl(String url) {
        return url.split("\\?")[0];
    }

    private void addEvent(Event event) {
        eventList.add(event);
    }

    private void sendData() throws IOException, URISyntaxException {
        URL url = new URI("http://localhost:8080/receiveJson").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String send = objectMapper.writeValueAsString(eventList);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = send.getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }
        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode);
        connection.disconnect();
        eventList.clear();
    }

    private String createWebElementId(WebElement element) {
        String id = element.getAttribute("id");

        if (!(id == null || Objects.equals(id, ""))) {
            return id;
        }

        String name = element.getAttribute("name");

        if (!(name == null || Objects.equals(name, ""))) {
            return name;
        }

        String text = element.getText();

        if (!(name == null || Objects.equals(text, ""))) {
            return text;
        }

        return element.getAttribute("class");
    }

    private String generateXPATH(WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if (childTag.equals("html")) {
            return "/html[1]" + current;
        }
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) {
                count++;
            }
            if (childElement.equals(childrenElement)) {
                return generateXPATH(parentElement, "/" + childTag + "[" + count + "]" + current);
            }
        }
        return null;
    }
}
