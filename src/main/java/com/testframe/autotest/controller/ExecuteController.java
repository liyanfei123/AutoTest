package com.testframe.autotest.controller;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.service.SceneExecuteService;
import com.testframe.autotest.ui.enums.BrowserEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/autotest")
public class ExecuteController {

    @Autowired
    private SceneExecuteService sceneExecuteService;

    // 执行场景
    // 没有步骤的场景不允许执行
    @GetMapping("/scene/execute")
    public HttpResult<Boolean> executeScene(@RequestParam(required = true) Long sceneId,
                                           @RequestParam(required = false) Integer browserType) {
        try {
            if (browserType == null) {
                browserType = BrowserEnum.CHROME.getType();
            }
            sceneExecuteService.executeScene(sceneId, browserType);
            return HttpResult.ok();
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @GetMapping("/set/execute")
    public HttpResult<Boolean> executeSet(@RequestParam(required = true) Long setId,
                                          @RequestParam(required = false) Integer browserType) {
        if (setId <= 0) {
            throw new AutoTestException("执行集id错误");
        }
        if (browserType == null) {
            browserType = BrowserEnum.CHROME.getType();
        }
        sceneExecuteService.executeSet(setId, browserType);
        return HttpResult.ok();
    }

}
