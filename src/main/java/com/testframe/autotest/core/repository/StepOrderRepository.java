package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.meta.convertor.SceneStepOrderConverter;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.core.repository.dao.StepOrderDao;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        return sceneStepOrders;
    }


}
