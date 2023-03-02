package com.testframe.autotest.meta.command;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStepsCmd {

    // 当前场景id,只能更新当前场景下的步骤
    private Long sceneId;

    private List<StepUpdateCmd> stepUpdateCmds;
}
