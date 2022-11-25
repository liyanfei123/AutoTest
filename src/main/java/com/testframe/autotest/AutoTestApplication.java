package com.testframe.autotest;

import com.testframe.autotest.core.config.TestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AutoTestApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(AutoTestApplication.class);
        builder.headless(false).run(args);
//        SpringApplication.run(AutoTestApplication.class, args);
    }

}
