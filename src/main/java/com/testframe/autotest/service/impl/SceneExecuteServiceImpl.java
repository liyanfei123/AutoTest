package com.testframe.autotest.service.impl;

import org.greenrobot.eventbus.EventBus;
import com.testframe.autotest.service.SceneExecuteService;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @date:2022/11/08 21:59
 * @author: lyf
 */
@Slf4j
@Service
@DependsOn("myEventBus")
public class SceneExecuteServiceImpl implements SceneExecuteService {

    @Autowired
    @Qualifier("myEventBus")
    private EventBus eventBus;

    @Override
    public void execute() {
        log.info("开始发送消息");
        eventBus.post(SeleniumRunEvent.builder().sceneId(123L).build());
    }
}
