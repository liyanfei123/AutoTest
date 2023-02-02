package com.testframe.autotest.meta.command;

import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.meta.bo.Step;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StepUpdateCmd {

    // 当前场景id
    private Long sceneId;

    // 步骤id，当新增步骤时为0
    private Long stepId = 0L;

    private Long sonSceneId = 0L;

    private String name;

    private String stepInfo;

    // 步骤执行状态 1:开启 2:关闭
    private Integer status;

    public static Step toStep(StepUpdateCmd stepUpdateCmd) {
        Step step = new Step();
        step.setStepId(stepUpdateCmd.getStepId());
        step.setSceneId(stepUpdateCmd.getSonSceneId());
        step.setStepName(stepUpdateCmd.getName());
        step.setStepInfo(stepUpdateCmd.getStepInfo());
        step.setStatus(stepUpdateCmd.getStatus());
        if (step.getStatus() == null) {
            step.setStatus(StepStatusEnum.OPEN.getType());
        }
        return step;
    }



}
