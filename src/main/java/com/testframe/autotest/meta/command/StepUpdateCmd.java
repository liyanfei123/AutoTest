package com.testframe.autotest.meta.command;

import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.meta.bo.Step;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StepUpdateCmd {

    private Long sceneId;

    private Long stepId;

    private String name;

    private String stepInfo;

    // 步骤执行状态 1:开启 2:关闭
    private Integer status;

    public static Step toStep(StepUpdateCmd stepUpdateCmd) {
        Step step = new Step();
        step.setStepId(stepUpdateCmd.getStepId());
        step.setStepName(stepUpdateCmd.getName());
        step.setStepInfo(stepUpdateCmd.getStepInfo());
        step.setStatus(stepUpdateCmd.getStatus());
        if (step.getStatus() == null) {
            step.setStatus(StepStatusEnum.OPEN.getType());
        }
        return step;
    }

}
