package com.github.paolobd.gamegui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

public class GameGui {
    public static WebDriver gamifyWebDriver(WebDriver driver) {
        WebDriverListener listener = new GamifiedListener(driver);
        return new EventFiringDecorator<>(listener).decorate(driver);
    }
}
