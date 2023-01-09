package com.testframe.autotest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AutoTestApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(AutoTestApplication.class);
        builder.headless(false).run(args);
//        SpringApplication.run(AutoTestApplication.class, args);
    }

}
