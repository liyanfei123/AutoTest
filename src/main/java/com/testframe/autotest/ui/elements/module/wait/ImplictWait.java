package com.testframe.autotest.ui.elements.module.wait;

import com.testframe.autotest.ui.elements.module.wait.base.BaseWait;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Description:
 * 隐式等待，直接对浏览器设置一个最大的等待时间
 * @date:2022/10/24 21:58
 * @author: lyf
 */
@Slf4j
@Component
public class ImplictWait extends BaseWait implements WaitI {

    @Override
    public String waitIdentity() {
        return WaitModeEnum.Implict_Wait.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        return;
    }

    @Override
    public void wait(By by, Integer time) {
        return;
    }

    @Override
    public void setTime(Integer time) {
        this.driverWait.withTimeout(Duration.ofSeconds(time));
    }

}
