package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.service.StepOrderInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        if (stepIds.size() == 0) {
            return;
        }
        SceneStepOrder sceneStepOrder = getStepBeforeOrder(sceneId);
        if (sceneStepOrder == null) {
            // 新增
            log.info("[StepOrderImpl:updateStepOrder] add step run order, order = {}", JSON.toJSONString(stepIds));
            sceneStepOrder = SceneStepOrder.build(sceneId, stepIds.toString());
            stepOrderRepository.saveSceneStepOrder(sceneStepOrder);
        } else {
            // 更新
            log.info("[StepOrderImpl:updateStepOrder] update step run order, order = {}", JSON.toJSONString(stepIds));
            sceneStepOrder.setOrderList(stepIds.toString());
            stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
        }
    }

    @Override
    public void removeStepId(Long sceneId, Long stepId) {
        SceneStepOrder sceneStepOrder = getStepBeforeOrder(sceneId);
        String stepOrder = sceneStepOrder.getOrderList();
        if (stepOrder == null || stepOrder.equals("")) {
            throw new AutoTestException("当前场景无可删除步骤");
        }
        stepOrder = stepOrder.substring(1, stepOrder.length()-1);
        String[] oldOrder = stepOrder.split(",");
        List<Long> newOrder = new ArrayList<>();
        for (String order : oldOrder) {
            if (Long.parseLong(order) != stepId) {
                newOrder.add(Long.parseLong(order));
            }
        }
        log.info("[StepOrderImpl:removeStepId] update step run order, oldOrder = {}, newOrder = {}",
                JSON.toJSONString(oldOrder), JSON.toJSONString(newOrder));
        sceneStepOrder.setOrderList(newOrder.toString());
        stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
    }

    // 获取执行前的顺序
    private SceneStepOrder getStepBeforeOrder(Long sceneId) {
        List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
        sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.BEFORE.getType());
        if (sceneStepOrders.size() > 1) {
            throw new AutoTestException("当前场景步骤执行顺序存在脏数据, 请手动处理, sceneId=" + sceneId);
        }
        if (sceneStepOrders.size() == 0) {
            return null;
        }
        return sceneStepOrders.get(0);
    }

    // 获取步骤执行时的顺序
    private List<SceneStepOrder> getStepRunOrder(Long sceneId) {
        List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
        sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.ING.getType());
        return sceneStepOrders;
    }

}
