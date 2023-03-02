package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.meta.convertor.SceneStepConverter;
import com.testframe.autotest.core.meta.convertor.StepOrderConverter;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.core.repository.dao.StepOrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StepOrderRepository {

    @Autowired
    private StepOrderDao stepOrderDao;

    @Autowired
    private StepOrderConverter stepOrderConverter;

    @Autowired
    private SceneStepConverter sceneStepConverter;

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSceneStepOrder(StepOrderDo stepOrderDo) {
        StepOrder stepOrder = stepOrderConverter.DoToPo(stepOrderDo);
        return stepOrderDao.updateStepOrder(stepOrder);

    }

    public List<StepOrderDo> queryStepOrderBySceneId(Long sceneId) {
        List<StepOrder> stepOrders = stepOrderDao.getStepOrderBySceneId(sceneId);
        List<StepOrderDo> stepOrderDos = stepOrders.stream().map(stepOrderConverter::PoToDo)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(stepOrderDos)) {
            return Collections.EMPTY_LIST;
        }
        return stepOrderDos;
    }

    public StepOrderDo queryBeforeStepRunOrder(Long sceneId) {
        List<StepOrderDo> stepOrderDos = queryStepOrderBySceneId(sceneId);
        if (stepOrderDos.isEmpty()) {
            return null;
        }
        stepOrderDos = stepOrderDos.stream().filter(sceneStepOrder -> sceneStepOrder.getType().equals(
                StepOrderEnum.BEFORE.getType()
        )).collect(Collectors.toList());
        if (stepOrderDos.size() > 1) {
            throw new AutoTestException("当前场景执行顺序存在脏数据");
        }
        return stepOrderDos.get(0);
    }

    public HashMap<Long, StepOrderDo> batchQueryStepExeOrderByRecordIds(List<Long> recordIds) {
        HashMap<Long, StepOrderDo> stepExeOrderMap = new HashMap<>();
        for (Long recordId : recordIds) {
            StepOrder stepOrder = stepOrderDao.queryStepOrderByRecordId(recordId);
            stepExeOrderMap.put(recordId, stepOrderConverter.PoToDo(stepOrder));
        }
        return stepExeOrderMap;
    }


}
