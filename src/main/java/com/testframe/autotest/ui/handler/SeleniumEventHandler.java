package com.testframe.autotest.ui.handler;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
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
public class SeleniumEventHandler extends AbstractEventHandler<SeleniumRunEvent> {

//    @Subscribe
//    public void eventHandler(Object event) {
//        try {
//            Thread.sleep(2000);
//            log.info("[SeleniumEventHandler:eventHandler] start handle selenium test, event = {}", JSON.toJSONString(event));
//        } catch (InterruptedException e) {
//            log.error("[SeleniumEventHandler:eventHandler] handle selenium error, reason = {}", e);
//        }
//    }


    @Subscribe
    @Override
    public void eventHandler(SeleniumRunEvent event) {
         try {
             log.info("[SeleniumEventHandler:handler] get event info, {}", JSON.toJSONString(event));
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    private void execute() {
        return;
    }
}
