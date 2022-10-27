package com.testframe.autotest.ui.elements.wait;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.ui.enums.WaitEnum;
import com.testframe.autotest.ui.meta.context.WebDriverContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * 显式等待
 * 页面元素在页面中存在
 * @date:2022/10/24 22:13
 * @author: lyf
 */
@Slf4j
public class ExplicitPresenceWait extends BaseWait implements WaitElementI {

    public ExplicitPresenceWait(WebDriver driver) {
        super(driver);
    }

    public ExplicitPresenceWait(WebDriver driver, Integer time) {
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
