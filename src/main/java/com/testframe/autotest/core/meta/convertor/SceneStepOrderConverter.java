package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.testframe.autotest.util.StringUtils.orderToList;

@Component
public class SceneStepOrderConverter {

    public StepOrder toPo(SceneStepOrder sceneStepOrder) {
        StepOrder stepOrder = new StepOrder();
        stepOrder.setId(sceneStepOrder.getId());
        stepOrder.setSceneId(sceneStepOrder.getSceneId());
        if (sceneStepOrder.getOrderList() != null && !sceneStepOrder.getOrderList().isEmpty()) {
            stepOrder.setOrderList(sceneStepOrder.getOrderList().toString());
        } else {
            stepOrder.setOrderList(Collections.EMPTY_LIST.toString());
        }
        if (sceneStepOrder.getType() == null || !StepOrderEnum.contains(sceneStepOrder.getType())) {
            stepOrder.setType(StepOrderEnum.BEFORE.getType());
        } else {
            stepOrder.setType(sceneStepOrder.getType());
        }
        stepOrder.setRecordId(sceneStepOrder.getRecordId());
        if (stepOrder.getRecordId() == null) {
            stepOrder.setRecordId(0L);
        }
        return stepOrder;
    }

    public SceneStepOrder PoToDo(StepOrder stepOrder) {
        SceneStepOrder sceneStepOrder = new SceneStepOrder();
        sceneStepOrder.setId(stepOrder.getId());
        sceneStepOrder.setSceneId(stepOrder.getSceneId());
        sceneStepOrder.setOrderList(orderToList(stepOrder.getOrderList()));
        sceneStepOrder.setType(stepOrder.getType());
        return sceneStepOrder;
    }
}
