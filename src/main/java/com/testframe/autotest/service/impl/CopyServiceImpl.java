package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.service.CopyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/10/30 20:47
 * @author: lyf
 */
@Slf4j
@Service
public class CopyServiceImpl implements CopyService {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long sceneCopy(Long sceneId) {
        log.info("[SceneCopyServiceImpl:copy] copy scene {}", sceneId);
        try {
            Scene scene = sceneDetailRepository.querySceneById(sceneId);
            if (scene == null) {
                throw new AutoTestException("当前场景不存在，不支持复制，请重新选择");
            }
            // 复制场景信息
            scene.setId(null);
            Long newSceneId = sceneDetailRepository.saveScene(scene);
            // 复制场景步骤
            List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            List<Long> stepIds = sceneStepRels.stream().map(SceneStepRel::getStepId).collect(Collectors.toList());
            // 查询当前场景下的所有步骤并进行复制
            List<Step> originSteps = stepDetailRepository.queryStepByIds(stepIds);
            originSteps.forEach(step -> step.setStepId(null));
            List<Long> newStepIds = stepDetailRepository.batchSaveStep(originSteps);
            // 构建新场景绑定关系,步骤默认为开启状态
            List<SceneStepRel> newSceneStepRels = new ArrayList<>();
            for (Long newStepId : newStepIds) {
                SceneStepRel sceneStepRel = new SceneStepRel();
                sceneStepRel.setSceneId(newSceneId);
                sceneStepRel.setStatus(StepStatusEnum.OPEN.getType());
                sceneStepRel.setStepId(newStepId);
                sceneStepRel.setIsDelete(0);
                newSceneStepRels.add(sceneStepRel);
            }
            sceneStepRepository.batchSaveSceneStep(newSceneStepRels);
            // 保存执行步骤
            SceneStepOrder sceneStepOrder = new SceneStepOrder();
            sceneStepOrder.setSceneId(newSceneId);
            sceneStepOrder.setOrderList(newStepIds.toString());
            sceneStepOrder.setType(StepOrderEnum.BEFORE.getType());
            stepOrderRepository.saveSceneStepOrder(sceneStepOrder);
            return newSceneId;
        } catch (Exception e) {
            log.error("[SceneCopyServiceImpl:copy] copy scene {} error, reason = {}", sceneId, e.getStackTrace());
            throw new AutoTestException("场景复制失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long stepCopy(Long sceneId, Long stepId) {
        log.info("[CopyServiceImpl:stepCopy] copy step in sceneId {} with stepId {}", sceneId, stepId);
        try {
            // 复制步骤
            Step copyStep = stepDetailRepository.queryStepById(stepId);
            copyStep.setStepId(null);
            Long newStepId = stepDetailRepository.saveStep(copyStep);
            copyStep.setStepId(newStepId);
            // 绑定关联关系
            sceneStepRepository.saveSceneStep(SceneStepRel.build(sceneId, copyStep));
            // 更新场景的执行顺序
            List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
            sceneStepOrders.stream().filter(sceneStepOrder -> sceneStepOrder.getType().equals(
                    StepOrderEnum.BEFORE.getType()
            ));
            SceneStepOrder newSceneStepOrder;
            if (sceneStepOrders.isEmpty()) {
                List<Long> stepIds = new ArrayList<>();
                stepIds.add(newStepId);
                newSceneStepOrder = SceneStepOrder.build(sceneId, stepIds.toString());
                stepOrderRepository.saveSceneStepOrder(newSceneStepOrder);
            } else {
                newSceneStepOrder = sceneStepOrders.get(0);
                List<Long> orderList = SceneStepOrder.orderToList(newSceneStepOrder.getOrderList());
                int index = orderList.indexOf(stepId);
                orderList.add(index+1, newStepId);
                newSceneStepOrder.setOrderList(orderList.toString());
                stepOrderRepository.updateSceneStepOrder(newSceneStepOrder);
            }
            return newStepId;
        } catch (Exception e) {
            log.error("[CopyServiceImpl:stepCopy] copy step {} error, reason",
                    stepId, e.getStackTrace());
            throw new AutoTestException("步骤复制失败");
        }
    }
}
