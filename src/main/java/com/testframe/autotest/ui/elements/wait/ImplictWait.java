package com.testframe.autotest.ui.elements.wait;

import com.testframe.autotest.ui.enums.WaitEnum;
import com.testframe.autotest.ui.meta.context.WebDriverContext;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
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
public class ImplictWait extends BaseWait implements WaitElementI {

    public ImplictWait(WebDriver driver) {
        super(driver);
    }

    public ImplictWait(WebDriver driver, Integer time) {
        super(driver, time);
    }

    @Override
    public String waitIdentity() {
        return WaitEnum.Implict_Wait.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        return;
    }



}