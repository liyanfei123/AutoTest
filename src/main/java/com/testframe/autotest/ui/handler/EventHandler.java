package com.testframe.autotest.ui.handler;

import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Description:
 *
 * @date:2022/11/19 23:22
 * @author: lyf
 */
@Slf4j
@Component
public class EventHandler {

    @Autowired
    @Qualifier("myEventBus")
    private EventBus eventBus;

    @Autowired
    private SeleniumEventHandler seleniumEventHandler;

    @PostConstruct
    private void init() {
        log.info("开始注册当前监听器");
        eventBus.register(seleniumEventHandler);
    }

    @PreDestroy
    public void destroy() {
        eventBus.unregister(seleniumEventHandler);
    }

}
