package com.github.paolobd.gamegui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class GamifiedListener implements WebDriverListener {
    private final WebDriver driver;
    private final List<Event> eventList = new ArrayList<>();
    private String currentUrl = "";
    private Boolean passwordInserted = false;
    private final String[] lastInteractedElement = new String[2];

    public GamifiedListener(WebDriver driver) {
        this.driver = driver;
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

        addEvent(new Event("", currentUrl, EventType.TITLE));
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        WebDriverListener.super.afterFindElement(driver, locator, result);

        //System.out.println("Locator: " + locator.toString());
        String reference = locator.toString().split(":")[1].trim();

        if (locator instanceof By.ById) {
            addEvent(new Event(reference, currentUrl, EventType.LOCATOR_ID));
        } else if (locator instanceof By.ByName) {
            addEvent(new Event(reference, currentUrl, EventType.LOCATOR_NAME));
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

        for (WebElement element : result) {
            afterFindElement(driver, locator, element);
        }
    }

    @Override
    public void beforeQuit(WebDriver driver) {
        WebDriverListener.super.beforeQuit(driver);
        Logger logger = Logger.getLogger("");

        try {
            sendData();
            logger.info("Data successfully sent to the GameGUI plugin!");
        } catch (IOException | URISyntaxException e) {
            logger.warning("Communication with GameGUI plugin failed. " +
                    "Check that the plugin is installed and running!");
        }
    }

    @Override
    public void beforeClick(WebElement element) {
        WebDriverListener.super.beforeClick(element);

        lastInteractedElement[0] = createWebElementId(element);
        lastInteractedElement[1] = element.getAttribute("type");
    }

    @Override
    public void afterClick(WebElement element) {
        WebDriverListener.super.afterClick(element);

        //If clicking the button triggered a navigation I cannot retrieve the element in the current page
        String type = lastInteractedElement[1];

        String newUrl = extractUrl(driver.getCurrentUrl());

        if (Objects.equals(type, "submit")) {
            addEvent(new Event(lastInteractedElement[0], currentUrl, EventType.ELEMENT_CLICK));
            addEvent(new Event(lastInteractedElement[0], currentUrl, EventType.SUBMIT));
        } else {
            addEvent(new Event(lastInteractedElement[0], currentUrl, EventType.ELEMENT_CLICK));
        }

        //Must be improved. We assume that a user always attempts to click a button which is the login button
        //after inputting the password
        if (passwordInserted) {
            passwordInserted = false;
            addEvent(new Event("", currentUrl, EventType.LOGIN));
        }

        if (!Objects.equals(currentUrl, newUrl)) {
            currentUrl = newUrl;
            addEvent(new Event("", currentUrl, EventType.NAVIGATION));
        }

    }

    @Override
    public void beforeSubmit(WebElement element) {
        WebDriverListener.super.beforeSubmit(element);

        lastInteractedElement[0] = createWebElementId(element);
        lastInteractedElement[1] = element.getAttribute("type");
    }

    @Override
    public void afterSubmit(WebElement element) {
        WebDriverListener.super.afterSubmit(element);

        String newUrl = extractUrl(driver.getCurrentUrl());

        addEvent(new Event(lastInteractedElement[0], currentUrl, EventType.SUBMIT));

        if (passwordInserted) {
            passwordInserted = false;
            addEvent(new Event("", currentUrl, EventType.LOGIN));
        }

        if (!Objects.equals(currentUrl, newUrl)) {
            currentUrl = newUrl;
            addEvent(new Event("", currentUrl, EventType.NAVIGATION));
        }

    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        WebDriverListener.super.afterSendKeys(element, keysToSend);

        lastInteractedElement[0] = createWebElementId(element);
        lastInteractedElement[1] = element.getAttribute("type");
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        WebDriverListener.super.afterSendKeys(element, keysToSend);

        String id = lastInteractedElement[0];
        addEvent(new Event(id, currentUrl, EventType.ELEMENT_SEND_KEYS));

        String type = lastInteractedElement[1];

        if (Objects.equals(type, "password")) {
            passwordInserted = true;
        }
    }

    @Override
    public void beforeClear(WebElement element) {
        WebDriverListener.super.afterClear(element);

        lastInteractedElement[0] = createWebElementId(element);
        lastInteractedElement[1] = element.getAttribute("type");
    }

    @Override
    public void afterClear(WebElement element) {
        WebDriverListener.super.afterClear(element);

        String type = lastInteractedElement[1];

        if (Objects.equals(type, "password")) {
            passwordInserted = false;
        }
    }

    @Override
    public void afterGetAttribute(WebElement element, String name, String result) {
        WebDriverListener.super.afterGetAttribute(element, name, result);

        addEvent(new Event(createWebElementId(element), currentUrl, EventType.ELEMENT_ATTRIBUTE));
    }

    @Override
    public void afterIsSelected(WebElement element, boolean result) {
        WebDriverListener.super.afterIsSelected(element, result);

        addEvent(new Event(createWebElementId(element), currentUrl, EventType.ELEMENT_SELECTED));
    }

    @Override
    public void afterIsEnabled(WebElement element, boolean result) {
        WebDriverListener.super.afterIsEnabled(element, result);

        addEvent(new Event(createWebElementId(element), currentUrl, EventType.ELEMENT_ENABLED));
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        WebDriverListener.super.afterGetText(element, result);

        addEvent(new Event(createWebElementId(element), currentUrl, EventType.ELEMENT_TEXT));
    }


    @Override
    public void afterFindElement(WebElement element, By locator, WebElement result) {
        WebDriverListener.super.afterFindElement(element, locator, result);

        afterFindElement(driver, locator, result);
    }

    @Override
    public void afterFindElements(WebElement element, By locator, List<WebElement> result) {
        WebDriverListener.super.afterFindElements(element, locator, result);

        afterFindElements(driver, locator, result);
    }

    @Override
    public void afterIsDisplayed(WebElement element, boolean result) {
        WebDriverListener.super.afterIsDisplayed(element, result);

        addEvent(new Event(createWebElementId(element), currentUrl, EventType.ELEMENT_DISPLAYED));
    }

    @Override
    public void afterGetCssValue(WebElement element, String propertyName, String result) {
        WebDriverListener.super.afterGetCssValue(element, propertyName, result);

        addEvent(new Event(createWebElementId(element), currentUrl, EventType.ELEMENT_CSS));
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        WebDriverListener.super.afterTo(navigation, url);

        String newUrl = extractUrl(driver.getCurrentUrl());

        if (!Objects.equals(newUrl, currentUrl)) {
            addEvent(new Event("", newUrl, EventType.NAVIGATION));
            currentUrl = newUrl;
        }
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, URL url) {
        WebDriverListener.super.afterTo(navigation, url);

        afterTo(navigation, url.toString());
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        WebDriverListener.super.afterBack(navigation);

        String newUrl = extractUrl(driver.getCurrentUrl());

        if (!Objects.equals(newUrl, currentUrl)) {
            addEvent(new Event("", newUrl, EventType.NAVIGATION_BACK));
            currentUrl = newUrl;
        }
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        WebDriverListener.super.afterForward(navigation);

        String newUrl = extractUrl(driver.getCurrentUrl());

        if (!Objects.equals(newUrl, currentUrl)) {
            addEvent(new Event("", newUrl, EventType.NAVIGATION_FORWARD));
            currentUrl = newUrl;
        }
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        WebDriverListener.super.afterRefresh(navigation);

        addEvent(new Event("", currentUrl, EventType.NAVIGATION_REFRESH));
    }

    @Override
    public void afterAccept(Alert alert) {
        WebDriverListener.super.afterAccept(alert);

        addEvent(new Event("", currentUrl, EventType.ALERT));
    }

    @Override
    public void afterDismiss(Alert alert) {
        WebDriverListener.super.afterDismiss(alert);

        addEvent(new Event("", currentUrl, EventType.ALERT));
    }

    @Override
    public void afterGetText(Alert alert, String result) {
        WebDriverListener.super.afterGetText(alert, result);

        addEvent(new Event("", currentUrl, EventType.ALERT_TEXT));
    }

    @Override
    public void afterSendKeys(Alert alert, String text) {
        WebDriverListener.super.afterSendKeys(alert, text);

        addEvent(new Event("", currentUrl, EventType.ALERT_SEND_KEYS));
    }

    private String extractUrl(String url) {
        try {
            URL site = new URI(url).toURL();
            return site.getHost() + site.getPath();
        } catch (Exception e) {
            return url;
        }
    }

    private void addEvent(Event event) {
        eventList.add(event);
    }

    private void sendData() throws IOException, URISyntaxException {
        URL url = new URI("http://localhost:8080/sendEvents").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String send = objectMapper.writeValueAsString(eventList);
        //System.out.println("EventList: " + eventList);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = send.getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }
        connection.getResponseCode();
        connection.disconnect();
        //A new GamifiedListener will be attached to the new WebDriver hence the clear is not stricyly necessary
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

        String xpath = generateXPATH(element, "");

        if (xpath != null) {
            return xpath;
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
