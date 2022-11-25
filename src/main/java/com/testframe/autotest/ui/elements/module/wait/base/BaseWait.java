package com.testframe.autotest.ui.elements.module.wait.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Description:
 *
 * @date:2022/10/25 22:30
 * @author: lyf
 */
public class BaseWait {

    public WebDriverWait driverWait;

    public WebDriver driver;

    public String identity;

    public BaseWait() {}

    public BaseWait(WebDriver driver) {
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public BaseWait(WebDriver driver, Integer time) {
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(time));
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.driver = webDriver;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setWebDriverWait(WebDriver driver, Integer time) {
        setWebDriver(driver);
        if (time != null && time > 0) {
            this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(time));
            return;
        }
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(0));
    }
}
