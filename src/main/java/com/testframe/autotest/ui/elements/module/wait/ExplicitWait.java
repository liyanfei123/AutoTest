package com.testframe.autotest.ui.elements.module.wait;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.wait.base.BaseWait;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Description:
 * 显式等待
 * 页面元素在页面中存在
 * @date:2022/10/24 22:13
 * @author: lyf
 */
@Slf4j
@Component
public class ExplicitWait extends BaseWait implements WaitI {

    @Override
    public String waitIdentity() {
        return WaitModeEnum.Explicit_Wait.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        try {
            log.info("[ExplicitWait:wait] wait by by, by = {}", JSON.toJSONString(by));
            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            throw new SeleniumRunException("显式等待-元素控件未出现");
        }
    }

    @Override
    public void wait(By by, Integer time) {
        try {
            log.info("[ExplicitPresenceWait:wait] wait by by & time, by = {}, time = {}",
                    by, time);
            this.driverWait.withTimeout(Duration.ofSeconds(time));
            wait(by);
        } catch (SeleniumRunException e) {
            throw new SeleniumRunException(e.getMessage());
        }
    }

    @Override
    public void setTime(Integer time) {
        this.driverWait.withTimeout(Duration.ofSeconds(time));
    }
}
