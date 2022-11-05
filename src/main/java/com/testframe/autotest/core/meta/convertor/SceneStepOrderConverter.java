package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import org.springframework.stereotype.Component;

@Component
public class SceneStepOrderConverter {

    public StepOrder toPo(SceneStepOrder sceneStepOrder) {
        StepOrder stepOrder = new StepOrder();
        stepOrder.setId(sceneStepOrder.getId());
        stepOrder.setSceneId(stepOrder.getSceneId());
        if (sceneStepOrder.getOrderStr() == null || sceneStepOrder.getOrderStr() == "") {
            stepOrder.setOrderList(null);
        } else {
            stepOrder.setOrderList(stepOrder.getOrderList());
        }
        if (sceneStepOrder.getType() == null || !StepOrderEnum.contains(sceneStepOrder.getType())) {
            stepOrder.setType(StepOrderEnum.BEFORE.getType());
        } else {
            stepOrder.setType(sceneStepOrder.getType());
        }
        return stepOrder;
    }

    public SceneStepOrder PoToDo(StepOrder stepOrder) {
        SceneStepOrder sceneStepOrder = new SceneStepOrder();
        sceneStepOrder.setSceneId(stepOrder.getSceneId());
        sceneStepOrder.setOrderStr(stepOrder.getOrderList());
        sceneStepOrder.setType(stepOrder.getType());
        sceneStepOrder.setId(null);
        return sceneStepOrder;
    }
}
