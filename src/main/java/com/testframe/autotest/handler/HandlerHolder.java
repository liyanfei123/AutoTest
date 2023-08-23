package com.testframe.autotest.handler;

import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.handler.base.DefaultHandlerChain;
import com.testframe.autotest.handler.execute.ExecuteHandler;
import com.testframe.autotest.handler.execute.SeleniumRunEventHandler;
import com.testframe.autotest.handler.execute.other.SceneExecuteRecordHandler;
import com.testframe.autotest.handler.execute.other.SetExecuteRecordHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class HandlerHolder {

    @Resource
    private ExecuteHandler executeHandler;

    @Resource
    private SeleniumRunEventHandler seleniumRunEventHandler;

    private final DefaultHandlerChain<ExecuteChannel> seleniumEventChain = new DefaultHandlerChain<>();

    @PostConstruct
    public void init() {
        seleniumEventChain.setNext(executeHandler)
                .setNext(seleniumRunEventHandler);
    }

    public DefaultHandlerChain<ExecuteChannel> getSeleniumEventChain() {
        return seleniumEventChain;
    }
}
