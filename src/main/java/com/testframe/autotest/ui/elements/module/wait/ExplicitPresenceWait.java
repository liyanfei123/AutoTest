package com.testframe.autotest.ui.elements.module.wait;

import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.wait.base.BaseWait;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 显式等待
 * 页面元素在页面中存在
 * @date:2022/10/24 22:13
 * @author: lyf
 */
@Slf4j
@Component
public class ExplicitPresenceWait extends BaseWait implements WaitI {

    @Override
    public String waitIdentity() {
        return WaitModeEnum.Presence_Element_Located.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        try {
            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("元素控件未出现");
        }
    }

    @Override
    public void wait(By by, Integer time) {
        return;
    }
}
