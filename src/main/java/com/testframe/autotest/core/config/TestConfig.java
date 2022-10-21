package com.testframe.autotest.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TestConfig {

    @Value("${test.content}")
    public String content;

}
