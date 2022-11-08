package com.testframe.autotest.ui.handler;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Description:
 *
 * @date:2022/11/08 21:51
 * @author: lyf
 */
@Slf4j
public abstract class AbstractEventHandler<R> {

    @Autowired
    private EventBus eventBus;

    @PostConstruct
    private void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class clazz = this.getClass();
        Class newClass = Class.forName(clazz.getName());
        Object registerClass = newClass.newInstance();
        log.info("注册事件处理器 {}", registerClass);
        eventBus.register(registerClass);
    }

    public abstract void eventHandler(Object event);

}
