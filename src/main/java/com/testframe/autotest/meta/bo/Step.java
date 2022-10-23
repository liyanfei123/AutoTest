package com.testframe.autotest.meta.bo;

import com.testframe.autotest.command.StepUpdateCmd;
import lombok.Data;

@Data
public class Step {

    private Long stepId;

    private String stepName;

    private String StepInfo;

    private Integer status;

}
