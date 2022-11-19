package com.testframe.autotest.ui.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @date:2022/11/08 21:51
 * @author: lyf
 */
public interface EventHandlerI<T> {

//    @PostConstruct
//    private void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        Class clazz = this.getClass();
//        Class newClass = Class.forName(clazz.getName());
//        Object registerClass = newClass.newInstance();
//        log.info("注册事件处理器 {}", registerClass);
//        eventBus.register(registerClass);
//    }

    public abstract void eventHandler(T event);

}
