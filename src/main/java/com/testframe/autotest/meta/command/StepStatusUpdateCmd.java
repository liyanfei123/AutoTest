package com.testframe.autotest.meta.command;

import lombok.Data;

@Data
public class StepStatusUpdateCmd {

    private Long sceneId;

    private Long stepId;

    private Integer status;

    // 1：单步骤修改 2:修改场景下的所有步骤
    private Integer type;

}
