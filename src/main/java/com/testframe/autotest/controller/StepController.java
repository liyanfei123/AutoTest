package com.testframe.autotest.controller;


import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.service.SceneStepService;
import com.testframe.autotest.service.impl.CopyServiceImpl;
import com.testframe.autotest.service.impl.StepDetailImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/autotest/step")
public class StepController {

    private static Logger logger = LoggerFactory.getLogger(StepController.class);

    @Autowired
    private StepDetailImpl stepDetail;

    @Autowired
    private SceneStepService sceneStepService;

    @Autowired
    private CopyServiceImpl stepCopyService;

    // 单场景步骤保存/更新
    @PostMapping("/save")
    public HttpResult<Boolean> stepSave(@RequestBody StepUpdateCmd stepUpdateCmd) {
        try {
            Long stepId = stepDetail.saveStepDetail(stepUpdateCmd);
            return HttpResult.ok(stepId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 单步骤删除
    @GetMapping("/deleteStep")
    public HttpResult<Boolean> deleteStep(@RequestParam(required = true) Long stepId) {
        if (stepId == null) {
            return HttpResult.error("请输入步骤id");
        }
        try {
            sceneStepService.removeSceneStepRelWithOrder(stepId);
            return HttpResult.ok("步骤删除成功");
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 改变步骤状态
    @GetMapping("/changeStatus")
    public HttpResult<Boolean> changeStatus(@RequestParam(required = true) Long stepId,
                                            @RequestParam(required = true) Long sceneId,
                                            @RequestParam(required = true) int status) {
        try {
            sceneStepService.changeStepStatus(stepId, sceneId, status);
            return HttpResult.ok();
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @GetMapping("/copy")
    public HttpResult<Long> copyStep(@RequestParam(required = false) Long sceneId,
                                     @RequestParam(required = true) Long stepId) {
        try {
            Long newSceneId = stepCopyService.stepCopy(sceneId, stepId);
            return HttpResult.ok(newSceneId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }
}
