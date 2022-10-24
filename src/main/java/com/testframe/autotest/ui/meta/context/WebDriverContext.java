package com.testframe.autotest.ui.meta.context;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 在同一个测试线程中使用同一个浏览器
 * @date:2022/10/24 22:04
 * @author: lyf
 */
@Component
public class WebDriverContext {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();

    public static void driverClear() {
        driverThreadLocal.remove();
    }

    public static void waitClear() {
        waitThreadLocal.remove();
    }

    public static void setDriverThreadLocal(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static void setWaitThreadLocal(WebDriverWait wait) {
        waitThreadLocal.set(wait);
    }

    public static WebDriver getDriverThreadLocal() {
        return driverThreadLocal.get();
    }

    public static WebDriverWait getWaitThreadLocal() {
        return waitThreadLocal.get();
    }

}
