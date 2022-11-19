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

    public Integer time;

    public WebDriver driver;

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

    public void setTime(Integer time) {
        this.time = time;
    }
}
