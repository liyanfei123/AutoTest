package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.meta.po.StepOrder;
import org.springframework.stereotype.Component;

import static com.testframe.autotest.util.StringUtils.orderToList;

@Component
public class StepOrderConverter {

    public StepOrder DoToPo(StepOrderDo stepOrderDo) {
        StepOrder stepOrder = new StepOrder();
        stepOrder.setId(stepOrderDo.getId());
        stepOrder.setSceneId(stepOrderDo.getSceneId());
        stepOrder.setRecordId(stepOrderDo.getRecordId());
        stepOrder.setOrderList(stepOrderDo.getOrderList().toString());
        stepOrder.setType(stepOrderDo.getType());
        return stepOrder;
    }

    public StepOrderDo PoToDo(StepOrder stepOrder) {
        StepOrderDo stepOrderDo = new StepOrderDo();
        stepOrderDo.setId(stepOrder.getId());
        stepOrderDo.setSceneId(stepOrder.getSceneId());
        stepOrderDo.setRecordId(stepOrder.getRecordId());
        stepOrderDo.setOrderList(orderToList(stepOrder.getOrderList()));
        stepOrderDo.setType(stepOrder.getType());
        return stepOrderDo;
    }
}
