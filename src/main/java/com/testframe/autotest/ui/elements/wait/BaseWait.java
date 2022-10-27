package com.testframe.autotest.ui.elements.wait;

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

    public BaseWait() {}

    public BaseWait(WebDriver driver) {
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public BaseWait(WebDriver driver, Integer time) {
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(time));
    }


}
