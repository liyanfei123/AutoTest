package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.meta.bo.SceneStepRel;
import org.springframework.stereotype.Component;

@Component
public class SceneStepConverter {

    public SceneStep toPO(SceneStepRel sceneStepRel) {
        SceneStep sceneStep = new SceneStep();
        sceneStep.setSceneId(sceneStep.getSceneId());
        sceneStep.setStepId(sceneStep.getStepId());
        sceneStep.setIsDelete(0);
        sceneStep.setStatus(StepStatusEnum.OPEN.getType());
        return sceneStep;
    }


}
