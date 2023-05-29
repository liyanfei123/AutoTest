package com.testframe.autotest.controller;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.domain.sceneStep.SceneStepDomain;
import com.testframe.autotest.service.SceneExecuteService;
import com.testframe.autotest.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试接口类
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private SceneExecuteService sceneExecuteService;

    @Resource
    private AutoTestConfig autoTestConfig;

    @Autowired
    private SceneStepDomain sceneStepDomain;

    @GetMapping("/hello")
    public String test() {
        logger.info("你好");
        return "hello world";
    }

    @GetMapping("/execute")
    public void execute() {
        sceneExecuteService.executeSet(123L, null);
    }

    @GetMapping("/config")
    public HttpResult getConfig() {
        try {
            Boolean value = autoTestConfig.getCopySwitch();
            System.out.println("get " + value);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getErrCode(), e.getMessage());
        }
        return null;
    }

    @GetMapping("/cookie")
    public String cookie(HttpServletResponse response) {
        // 拦截器通过后，添加cookie
        CookieUtil.addCookie(response, "hello", "world");
        return "Hello World!";
    }

    @GetMapping("fatherScene")
    public List<Long> fatherScene(@RequestParam Long sceneId) {
        List<Long> sceneIds = new ArrayList<Long>(){{add(sceneId);}};
        Map<Long, List<Long>> f = sceneStepDomain.scenesInOther(sceneIds);
        return f.get(sceneId);
    }

    @GetMapping("exception")
    public HttpResult exception() {
        throw new AutoTestException(12345, "自定义测试");
    }


}
