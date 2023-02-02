package com.testframe.autotest.meta.dto;


import com.testframe.autotest.meta.model.StepInfoModel;
import lombok.Data;

@Data
public class StepInfoDto {

    private Long stepId;

    // 只有是子场景时才有值，其他情况为0L
    private Long sonSceneId = 0L;

    private String stepName;

    private Integer stepStatus;

    // 用于区分当前步骤是否是子场景还是单步骤
    private Integer type;

    private StepUIInfo stepUIInfo;

}
