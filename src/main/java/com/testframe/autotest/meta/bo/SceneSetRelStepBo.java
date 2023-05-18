package com.testframe.autotest.meta.bo;

import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.Data;

@Data
public class SceneSetRelStepBo extends SceneSetRelStepDto {

    private Long relId;

    private String stepName;
}
