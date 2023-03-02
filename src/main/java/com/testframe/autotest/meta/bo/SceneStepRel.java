package com.testframe.autotest.meta.bo;

import com.testframe.autotest.core.enums.StepTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SceneStepRel {

    private Long id;

    private Long sceneId;

    private Long stepId;

    private Integer status;

    private Integer type;

    private Integer isDelete;

}
