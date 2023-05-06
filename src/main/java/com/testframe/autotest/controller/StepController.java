package com.testframe.autotest.controller;


import com.testframe.autotest.meta.command.StepOrderUpdateCmd;
import com.testframe.autotest.meta.command.StepStatusUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.command.UpdateStepsCmd;
import com.testframe.autotest.service.StepService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/autotest/step")
public class StepController {

    private static Logger logger = LoggerFactory.getLogger(StepController.class);

    @Autowired
    private StepService stepService;

    // 单场景步骤保存/更新
    @PostMapping("/add")
    public HttpResult<List> stepSave(@RequestBody UpdateStepsCmd updateStepsCmd) {
        try {
            if (updateStepsCmd.getSceneId() == null || updateStepsCmd.getStepUpdateCmds() == null) {
                return HttpResult.error();
            }
            List<Long> stepIds = stepService.addStepDetail(updateStepsCmd);
            return HttpResult.ok(stepIds);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public HttpResult<Boolean> stepUpdate(@RequestBody UpdateStepsCmd updateStepsCmd) {
        try {
            Boolean flag = stepService.updateStepDetail(updateStepsCmd);
            return HttpResult.ok(flag);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 单步骤删除
    @GetMapping("/deleteStep")
    public HttpResult<Boolean> deleteStep(@RequestParam(required = true) Long sceneId,
                                          @RequestParam(required = true) Long stepId) {
        if (stepId == null) {
            return HttpResult.error("请输入步骤id");
        }
        try {
            return HttpResult.ok(stepService.removeStep(sceneId, stepId));
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 改变步骤状态
    @PostMapping("/changeStatus")
    public HttpResult<Boolean> changeStatus(@RequestBody StepStatusUpdateCmd stepStatusUpdateCmd) {
        try {
            if (stepStatusUpdateCmd.getSceneId() == null || stepStatusUpdateCmd.getStepId() == null) {
                return HttpResult.error("请输入场景或步骤");
            }
            stepService.changeStepStatus(stepStatusUpdateCmd);
            return HttpResult.ok();
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @GetMapping("/copy")
    public HttpResult<Long> copyStep(@RequestParam(required = true) Long sceneId,
                                     @RequestParam(required = true) Long stepId) {
        try {
            if (sceneId == null || sceneId == 0) {
                throw new AutoTestException("请输入场景id");
            }
            Long newStepId = stepService.stepCopy(sceneId, stepId);
            return HttpResult.ok(newStepId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 调整步骤执行顺序，批量调整
    @PostMapping("/order")
    public HttpResult<Boolean> changeOrder(@RequestBody StepOrderUpdateCmd stepOrderUpdateCmd) {
        try {
            if (stepOrderUpdateCmd.getSceneId() == null || stepOrderUpdateCmd.getSceneId() == 0L
            || stepOrderUpdateCmd.getOrders() == null || stepOrderUpdateCmd.getOrders().isEmpty()) {
                return HttpResult.ok("输入参数");
            }
            return HttpResult.ok(stepService.changeStepOrderList(stepOrderUpdateCmd));
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 调整步骤顺序，单个调整
    @GetMapping("/change")
    public HttpResult<Boolean> changeOrder(
            @RequestParam(required = true) Long sceneId,
            @RequestParam(required = true, defaultValue = "0") Long beforeStepId,
            @RequestParam(required = true) Long stepId,
            @RequestParam(required = true, defaultValue = "0") Long afterStepId) {
        try {
            if (sceneId == null || sceneId <= 0L || stepId == null || stepId <= 0L) {
                return HttpResult.ok("输入参数非法");
            }
            return HttpResult.ok(stepService.changeStepOrder(sceneId, beforeStepId, stepId, afterStepId));
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

}
