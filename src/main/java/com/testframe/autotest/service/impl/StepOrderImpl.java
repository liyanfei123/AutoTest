package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.service.StepOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/10/29 12:52
 * @author: lyf
 */
@Slf4j
@Service
public class StepOrderImpl implements StepOrderService {

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
            sceneStepOrder.setOrderList(stepIds);
            stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
        }
    }

    @Override
    public void updateStepOrder(Long sceneId, Long stepId) {
        try {
            List<Long> stepOrderList;
            SceneStepOrder sceneStepOrder = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            if (sceneStepOrder == null || sceneStepOrder.getOrderList().isEmpty()) {
                stepOrderList = new ArrayList<Long>(){{add(stepId);}};
            } else {
                stepOrderList = sceneStepOrder.getOrderList();
                stepOrderList.add(stepId);
            }
            updateStepOrder(sceneId, stepOrderList);
        } catch (Exception e) {
            log.error("[StepOrderImpl:updateStepOrder] add step order stepId {} error, reason = ", stepId, e);
            e.printStackTrace();
            throw new AutoTestException("单步骤执行顺序添加失败");
        }
    }


    @Override
    public void removeStepId(Long sceneId, Long stepId) {
        SceneStepOrder sceneStepOrder = getStepBeforeOrder(sceneId);
        List<Long> stepOrder = sceneStepOrder.getOrderList();
        if (stepOrder == null || stepOrder.isEmpty()) {
            throw new AutoTestException("当前场景无可删除步骤");
        }
        List<Long> newOrder = stepOrder.stream().filter(sId -> !sId.equals(stepId)).collect(Collectors.toList());
        log.info("[StepOrderImpl:removeStepId] update step run order, oldOrder = {}, newOrder = {}",
                JSON.toJSONString(stepOrder), JSON.toJSONString(newOrder));
        sceneStepOrder.setOrderList(newOrder);
        stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
    }

    // 查询当前的编排顺序
    @Override
    public List<Long> queryNowStepOrder(Long sceneId) {
        try {
            SceneStepOrder sceneStepOrder = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            List<Long> stepOrderList = sceneStepOrder.getOrderList();
            log.info("[StepOrderImpl:queryNowStepOrder] step order {}", JSON.toJSONString(stepOrderList));
            return stepOrderList;
        } catch (Exception e) {
            log.error("[StepOrderImpl:queryNowStepOrder] in scene {}, reason", sceneId, e);
            e.printStackTrace();
            throw new AutoTestException("查询当前场景下的步骤顺序失败");
        }
    }


    // 获取执行前的顺序
    @Deprecated
    private SceneStepOrder getStepBeforeOrder(Long sceneId) {
        List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
        sceneStepOrders = sceneStepOrders.stream().filter(
                sceneStepOrder -> sceneStepOrder.getType() == StepOrderEnum.BEFORE.getType())
                .collect(Collectors.toList());
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
        return sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.ING.getType())
                .collect(Collectors.toList());
    }

}
