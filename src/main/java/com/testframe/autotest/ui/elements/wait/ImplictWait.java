package com.testframe.autotest.ui.elements.wait;

import com.testframe.autotest.ui.enums.WaitEnum;
import com.testframe.autotest.ui.meta.context.WebDriverContext;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * 隐式等待，直接对浏览器设置一个最大的等待时间
 * @date:2022/10/24 21:58
 * @author: lyf
 */
@Component
@Slf4j
public class ImplictWait implements WaitElementI {

    @Override
    public String waitIdentity() {
        return WaitEnum.Implict_Wait.getWaitIdentity();
    }

    @Override
    public WebElement wait(By by) {
        WebDriver driver = WebDriverContext.getDriverThreadLocal();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver.findElement(by);
    }

    @Override
    public List<WebElement> waits(By by) {
        WebDriver driver = WebDriverContext.getDriverThreadLocal();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver.findElements(by);
    }
}
