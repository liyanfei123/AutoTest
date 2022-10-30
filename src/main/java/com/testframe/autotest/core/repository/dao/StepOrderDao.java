package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.core.repository.mapper.StepOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StepOrderDao {

    @Autowired
    private StepOrderMapper stepOrderMapper;

    public Boolean saveStepOrder(StepOrder stepOrder) {
        Long currentTime = System.currentTimeMillis();
        stepOrder.setCreateTime(currentTime);
        stepOrder.setUpdateTime(currentTime);
        return stepOrderMapper.insert(stepOrder) > 0 ? true : false;
    }


    // 仅有type=1的时候可以更新
    public Boolean updateStepOrder(StepOrder stepOrder) {
        Long currentTime = System.currentTimeMillis();
        stepOrder.setUpdateTime(currentTime);
        return stepOrderMapper.updateByPrimaryKey(stepOrder) > 0 ? true : false;

    }

    public List<StepOrder> getStepOrderBySceneId(Long sceneId) {
        List<StepOrder> stepOrders = stepOrderMapper.getStepOrderBySceneId(sceneId);
        if (CollectionUtils.isEmpty(stepOrders)) {
            return Collections.EMPTY_LIST;
        }
        return stepOrders;
    }
}
