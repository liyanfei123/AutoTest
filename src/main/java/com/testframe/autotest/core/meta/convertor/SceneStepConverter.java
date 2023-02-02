package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.meta.bo.SceneStepRel;
import org.springframework.stereotype.Component;

@Component
public class SceneStepConverter {

    public SceneStep toPO(SceneStepRel sceneStepRel) {
        SceneStep sceneStep = new SceneStep();
        sceneStep.setId(sceneStepRel.getId());
        sceneStep.setSceneId(sceneStepRel.getSceneId());
        sceneStep.setStepId(sceneStepRel.getStepId());
        sceneStep.setStatus(sceneStepRel.getStatus());
        sceneStep.setType(sceneStepRel.getType());
        sceneStep.setIsDelete(sceneStepRel.getIsDelete());
        if (sceneStep.getIsDelete() == null) {
            sceneStep.setIsDelete(0);
        }
        return sceneStep;
    }

    public SceneStepRel PoToDo(SceneStep sceneStep) {
        if (sceneStep == null) {
            return null;
        }
        SceneStepRel sceneStepRel = new SceneStepRel();
        sceneStepRel.setId(sceneStep.getId());
        sceneStepRel.setSceneId(sceneStep.getSceneId());
        sceneStepRel.setStepId(sceneStep.getStepId());
        sceneStepRel.setStatus(sceneStep.getStatus());
        sceneStepRel.setType(sceneStep.getType());
        sceneStepRel.setIsDelete(sceneStep.getIsDelete());
        return sceneStepRel;
    }


}
