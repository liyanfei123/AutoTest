package com.testframe.autotest.core.meta.Do;

import com.testframe.autotest.core.enums.StepTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SceneStepRelDo {

    private Long id;

    private Long stepId;

    private Long sceneId;

    private Integer status;

    private Integer type;

}
