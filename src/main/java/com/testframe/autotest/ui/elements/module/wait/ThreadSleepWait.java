package com.testframe.autotest.ui.elements.module.wait;

import com.testframe.autotest.ui.elements.module.wait.base.BaseWait;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 线程直接睡眠等待
 * @date:2022/10/24 22:13
 * @author: lyf
 */
@Slf4j
@Component
public class ThreadSleepWait extends BaseWait implements WaitI {

    @Override
    public String waitIdentity() {
        return WaitModeEnum.NO_WAIT.getWaitIdentity();
    }

    @Override
    public void wait(By by) {
        return;
    }

    @Override
    public void wait(By by, Integer time) {
        try {
            Thread.sleep(time*1000);
        } catch (Exception e) {
            return;
        }
    }
}
