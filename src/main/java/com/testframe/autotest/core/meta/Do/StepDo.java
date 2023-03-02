package com.testframe.autotest.core.meta.Do;

import com.testframe.autotest.core.meta.po.StepDetail;
import lombok.Data;

@Data
public class StepDo {

    private StepDetailDo stepDetailDo;

    private SceneStepRelDo sceneStepRelDo;
}
