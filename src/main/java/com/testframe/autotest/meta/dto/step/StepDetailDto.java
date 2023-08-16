package com.testframe.autotest.meta.dto.step;

import com.testframe.autotest.meta.model.StepInfoModel;
import lombok.Data;

@Data
public class StepDetailDto {

    private Long sceneId;

    private Long stepId;

    // 只有是子场景时才有值，其他情况为0L
    // 业务层判断子场景是否存在
    private Long sonSceneId = 0L;

    private String stepName;

    private Integer stepStatus;

    // 用于区分当前步骤是否是子场景还是单步骤
    private Integer type;


    /**
     * @see StepInfoModel
     */
    private String stepUIInfo;

}
