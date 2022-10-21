package com.testframe.autotest.command;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StepUpdateCmd {

    @NotNull
    private Long sceneId;

    private Long stepId;

    private String name;

    private String stepInfo;

    // 步骤执行状态 1:开启 2:关闭
    @NotNull
    private Integer status;

}
