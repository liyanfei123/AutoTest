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
public class ExplicitWait extends BaseWait implements WaitElementI{

    public ExplicitWait(WebDriver driver) {
        super(driver);
    }

    public ExplicitWait(WebDriver driver, Integer time) {
        super(driver, time);
    }

    @Override
    public String waitIdentity() {
        return WaitEnum.Presence_Element_Located.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        try {
            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException("元素控件未出现");
        }
    }
}
