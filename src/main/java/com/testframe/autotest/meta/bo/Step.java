package com.testframe.autotest.meta.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Step {

    private Long stepId;

    private String stepName;

    private String StepInfo;

    private Integer status;

}
