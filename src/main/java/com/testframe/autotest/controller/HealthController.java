package com.testframe.autotest.controller;


import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public HttpResult<Object> checkHealth() {
        return HttpResult.ok("hello");
    }
}
