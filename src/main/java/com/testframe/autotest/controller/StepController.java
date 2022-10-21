package com.testframe.autotest.controller;


import com.testframe.autotest.command.StepUpdateCmd;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/autotest/step")
public class StepController {

    private static Logger logger = LoggerFactory.getLogger(StepController.class);

    @Autowired

    // 单步骤保存
    @PostMapping("/save")
    public HttpResult<Boolean> stepSave(StepUpdateCmd stepUpdateCmd) {

        return null;
    }

    // 多步骤批量保存
    @PostMapping("/batchSave")
    public HttpResult<Boolean> batchSave() {
        return null;
    }

    // 单步骤删除
    @GetMapping("/deleteStep")
    public HttpResult<Boolean> deleteStep(Long stepId) {
        return null;
    }
}
