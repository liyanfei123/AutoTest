package com.testframe.autotest.ui.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @date:2022/11/08 21:55
 * @author: lyf
 */
@DependsOn("myEventBus")
@Component
@Slf4j
public class SeleniumEventHandler extends AbstractEventHandler {

    @Subscribe
    public void eventHandler(Object event) {
        try {
            Thread.sleep(2000);
            log.info("[SeleniumEventHandler:eventHandler] start handle selenium test, event = {}", JSON.toJSONString(event));
        } catch (InterruptedException e) {
            log.error("[SeleniumEventHandler:eventHandler] handle selenium error, reason = {}", e);
        }
    }
}
