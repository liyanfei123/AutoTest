package com.testframe.autotest.core.config;

import org.greenrobot.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean("myEventBus")
    public EventBus myEventBus() {
        return EventBus.getDefault();
    }

}
