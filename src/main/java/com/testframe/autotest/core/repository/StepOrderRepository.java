package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.convertor.SceneStepOrderConverter;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.core.repository.dao.StepOrderDao;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepOrder;
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
    private SceneStepOrderConverter sceneStepOrderConverter;

    @Transactional(rollbackFor = Exception.class)
    public Boolean saveSceneStepOrder(SceneStepOrder sceneStepOrder) {
        StepOrder stepOrder = sceneStepOrderConverter.toPo(sceneStepOrder);
        return stepOrderDao.saveStepOrder(stepOrder);
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSceneStepOrder(SceneStepOrder sceneStepOrder) {
        StepOrder stepOrder = sceneStepOrderConverter.toPo(sceneStepOrder);
        return stepOrderDao.updateStepOrder(stepOrder);

    }

    public List<SceneStepOrder> queryStepOrderBySceneId(Long sceneId) {
        List<StepOrder> stepOrders = stepOrderDao.getStepOrderBySceneId(sceneId);
        List<SceneStepOrder> sceneStepOrders = stepOrders.stream().map(sceneStepOrderConverter::PoToDo)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sceneStepOrders)) {
            return Collections.EMPTY_LIST;
        }
        return sceneStepOrders;
    }

    public SceneStepOrder queryBeforeStepRunOrder(Long sceneId) {
        List<SceneStepOrder> sceneStepOrders = queryStepOrderBySceneId(sceneId);
        if (sceneStepOrders.isEmpty()) {
            return null;
        }
        sceneStepOrders = sceneStepOrders.stream().filter(sceneStepOrder -> sceneStepOrder.getType().equals(
                StepOrderEnum.BEFORE.getType()
        )).collect(Collectors.toList());
        if (sceneStepOrders.size() > 1) {
            throw new AutoTestException("当前场景执行顺序存在脏数据");
        }
        return sceneStepOrders.get(0);
    }

    public HashMap<Long, SceneStepOrder> bacthQueryStepExeOrderByRecordIds(List<Long> recordIds) {
        HashMap<Long, SceneStepOrder> stepExeOrderMap = new HashMap<>();
        for (Long recordId : recordIds) {
            StepOrder stepOrder = stepOrderDao.queryStepOrderByRecordId(recordId);
            stepExeOrderMap.put(recordId, sceneStepOrderConverter.PoToDo(stepOrder));
        }
        return stepExeOrderMap;
    }


}
