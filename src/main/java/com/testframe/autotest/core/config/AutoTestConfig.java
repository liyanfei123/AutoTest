package com.testframe.autotest.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
public class AutoTestConfig {

    @Value("${copy.switch}")
    private Boolean copySwitch;
}
