package com.testframe.autotest.domain.stepOrder.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.stepOrder.StepOrderDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StepOrderDomainImpl implements StepOrderDomain {

    @Autowired
    private StepOrderRepository stepOrderRepository;


    @Override
    public Boolean changeOrder(Long sceneId, List<Long> newStepOrder) {
        StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
        if (stepOrderDo == null) {
            return true;
        }
        log.info("[StepOrderDomainImpl:changeOrder] old order = {}, new order = {}",
                JSON.toJSONString(stepOrderDo.getOrderList()), JSON.toJSONString(newStepOrder));
        stepOrderDo.setOrderList(newStepOrder);
        return stepOrderRepository.updateSceneStepOrder(stepOrderDo);
    }

    @Override
    public List<Long> stepOrderList(Long sceneId, Integer type) {
        if (type == StepOrderEnum.BEFORE.getType()) {
            StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
        }
        StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
        if (stepOrderDo == null) {
            return Collections.EMPTY_LIST;
        }
        return stepOrderDo.getOrderList();
    }
}
