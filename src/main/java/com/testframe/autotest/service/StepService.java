package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.StepOrderUpdateCmd;
import com.testframe.autotest.meta.command.StepStatusUpdateCmd;
import com.testframe.autotest.meta.command.UpdateStepsCmd;

import java.util.HashMap;
import java.util.List;

public interface StepService {

    List<Long> addStepDetail(UpdateStepsCmd stepUpdateCmd);

    Boolean updateStepDetail(UpdateStepsCmd updateStepsCmd);

    Boolean removeStep(Long sceneId, Long stepId);
    // 单步骤复制
    Long stepCopy(Long sceneId, Long stepId);

    // 改变步骤执行顺序
    Boolean changeStepOrderList(StepOrderUpdateCmd stepOrderUpdateCmd);

    Boolean changeStepOrder(Long sceneId, Long beforeStepId, Long stepId, Long afterStepId);

    Boolean changeStepStatus(StepStatusUpdateCmd stepStatusUpdateCmd);

}
