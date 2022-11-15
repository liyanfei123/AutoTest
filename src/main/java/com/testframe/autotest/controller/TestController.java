package com.testframe.autotest.controller;

import com.testframe.autotest.service.SceneExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private SceneExecuteService sceneExecuteService;

    @GetMapping("/hello")
    public String test() {
        logger.info("你好");
        return "hello world";
    }

    @GetMapping("/execute")
    public void execute() {
        sceneExecuteService.execute(123L);
    }



}
