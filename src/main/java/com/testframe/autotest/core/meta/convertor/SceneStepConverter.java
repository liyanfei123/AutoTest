package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.meta.po.SceneStep;
import org.springframework.stereotype.Component;

@Component
public class SceneStepConverter {

    public SceneStep DoToPO(SceneStepRelDo sceneStepRelDo) {
        SceneStep sceneStep = new SceneStep();
        sceneStep.setId(sceneStepRelDo.getId());
        sceneStep.setSceneId(sceneStepRelDo.getSceneId());
        sceneStep.setStepId(sceneStepRelDo.getStepId());
        sceneStep.setStatus(sceneStepRelDo.getStatus());
        sceneStep.setType(sceneStepRelDo.getType());
        sceneStep.setIsDelete(0);
        return sceneStep;
    }

    public SceneStepRelDo PoToDo(SceneStep sceneStep) {
        SceneStepRelDo sceneStepRelDo = new SceneStepRelDo();
        sceneStepRelDo.setId(sceneStep.getId());
        sceneStepRelDo.setStepId(sceneStep.getStepId());
        sceneStepRelDo.setSceneId(sceneStep.getSceneId());
        sceneStepRelDo.setStatus(sceneStep.getStatus());
        sceneStepRelDo.setType(sceneStep.getType());
        return sceneStepRelDo;
    }


}
