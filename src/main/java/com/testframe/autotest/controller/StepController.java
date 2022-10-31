package com.testframe.autotest.controller;


import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.service.impl.CopyServiceImpl;
import com.testframe.autotest.service.impl.SceneStepInterImpl;
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
    private SceneStepInterImpl sceneStepInter;

    @Autowired
    private CopyServiceImpl stepCopyService;

    // 单场景步骤保存/更新
    @PostMapping("/save")
    public HttpResult<Boolean> stepSave(@RequestBody StepUpdateCmd stepUpdateCmd) {
        Long flag = stepDetail.saveStepDetail(stepUpdateCmd);
        if (flag != null) {
            return HttpResult.ok();
        }
        return HttpResult.error("步骤更新失败");
    }

    // 多步骤批量保存
    @PostMapping("/batchSave")
    public HttpResult<Boolean> batchSave() {
        return null;
    }

    // 单步骤删除
    @GetMapping("/deleteStep")
    public HttpResult<Boolean> deleteStep(@RequestParam(required = true) Long stepId) {
        if (stepId == null) {
            return HttpResult.error("请输入步骤id");
        }
        try {
            sceneStepInter.removeSceneStepRel(stepId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
        return HttpResult.ok();
    }

    @GetMapping("/copy")
    public HttpResult<Long> copyStep(@RequestParam(required = true) Long stepId) {
        return HttpResult.ok();
    }
}
