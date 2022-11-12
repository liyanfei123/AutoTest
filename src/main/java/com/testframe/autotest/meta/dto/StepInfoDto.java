package com.testframe.autotest.meta.dto;


import com.testframe.autotest.meta.model.StepInfoModel;
import lombok.Data;

@Data
public class StepInfoDto {

    private Long stepId;

    private String stepName;

    private Integer stepStatus;

    private StepUIInfo stepUIInfo;

}
