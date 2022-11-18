package com.testframe.autotest.ui.elements.wait;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.ui.enums.wait.WaitEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Description:
 * 显式等待
 * 页面元素在页面中存在
 * @date:2022/10/24 22:13
 * @author: lyf
 */
@Slf4j
public class NoWait extends BaseWait implements WaitElementI {

    public NoWait(WebDriver driver) {
        super(driver);
    }

    public NoWait(WebDriver driver, Integer time) {
        super(driver, time);
    }

    @Override
    public String waitIdentity() {
        return WaitEnum.NO_WAIT.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        return;
    }

    @Override
    public void wait(By by, Integer time) {
        return;
    }
}
