package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.service.StepOrderInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/29 12:52
 * @author: lyf
 */
@Slf4j
@Service
public class StepOrderImpl implements StepOrderInter  {

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Override
    public void updateStepOrder(Long sceneId, List<Long> stepIds) {
        List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
        sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.BEFORE.getType());
        SceneStepOrder sceneStepOrder;
        Integer len = sceneStepOrders.size();
        switch (len) {
            case 0:
                // 新增
                sceneStepOrder = SceneStepOrder.build(sceneId, stepIds.toString());
                stepOrderRepository.saveSceneStepOrder(sceneStepOrder);
                break;
            case 1:
                // 更新
                sceneStepOrder = sceneStepOrders.get(0);
                sceneStepOrder.setOrderList(stepIds.toString());
                stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
                break;
            default:
                throw new AutoTestException("当前执行顺序表存在脏数据");
        }
    }

}
