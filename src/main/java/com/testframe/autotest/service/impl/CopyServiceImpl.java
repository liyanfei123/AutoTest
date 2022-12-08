package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.*;
import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.service.CopyService;
import com.testframe.autotest.service.SceneStepService;
import com.testframe.autotest.service.StepDetailService;
import com.testframe.autotest.service.StepOrderService;
import com.testframe.autotest.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private AutoTestConfig autoTestConfig;
    @Autowired
    private StepDetailService stepDetailService;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private SceneStepService sceneStepService;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private StepOrderService stepOrderService;

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
            String sceneSuffix = RandomUtil.randomCode(8);
            scene.setTitle(scene.getTitle() + sceneSuffix);
            Long newSceneId = sceneDetailRepository.saveScene(scene);
            // 复制场景步骤
            List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            if (sceneStepRels.isEmpty()) {
                // 当前场景下没有步骤需要保存
                return newSceneId;
            }

            List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
            sceneStepOrders = sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.BEFORE.getType())
                    .collect(Collectors.toList());
            List<Long> orgStepOrder = sceneStepOrders.get(0).getOrderList();
            // 查询当前场景下的所有步骤并进行复制
            List<Step> originSteps = stepDetailRepository.queryStepByIds(orgStepOrder);
            // 不改变步骤编排顺序
            Collections.sort(originSteps, new Comparator<Step>() {
                @Override
                public int compare(Step step1, Step step2) {
                    return orgStepOrder.indexOf(step1.getStepId()) - orgStepOrder.indexOf(step2.getStepId());
                }
            });
            originSteps.forEach(step -> {
                step.setStepId(null);
                String stepSuffix = RandomUtil.randomCode(8);
                step.setStepName(step.getStepName() + stepSuffix);
            });
            List<Long> newStepIds = stepDetailRepository.batchSaveStep(originSteps);
            // 构建新场景绑定关系,步骤默认为开启状态
            sceneStepService.batchSaveSceneStep(newStepIds, sceneId);

            // 保存执行步骤
            SceneStepOrder sceneStepOrder = new SceneStepOrder();
            sceneStepOrder.setSceneId(newSceneId);
            sceneStepOrder.setOrderList(newStepIds);
            sceneStepOrder.setType(StepOrderEnum.BEFORE.getType());
            stepOrderRepository.saveSceneStepOrder(sceneStepOrder);

            return newSceneId;
        } catch (Exception e) {
            log.error("[SceneCopyServiceImpl:copy] copy scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException("场景复制失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long stepCopy(Long sceneId, Long stepId) {
        log.info("[CopyServiceImpl:stepCopy] copy step in sceneId {} with stepId {}", sceneId, stepId);
        try {
            Step copyStep = stepDetailRepository.queryStepById(stepId);
            if (copyStep == null) {
                throw new AutoTestException("当前被复制的步骤不存在");
            }
            Long newStepId;
            if (!autoTestConfig.getCopySwitch()) {
                // 被复制到最后面，默认开启态
                StepUpdateCmd newStep = new StepUpdateCmd(sceneId, null,
                        copyStep.getStepName() + RandomUtil.randomCode(8),
                        copyStep.getStepInfo(), StepStatusEnum.OPEN.getType());
                newStepId = stepDetailService.saveStepDetail(newStep);
            } else {
                // 紧贴着复制步骤，并且保持状态一致
                Step newStep = new Step(null, copyStep.getStepName() + RandomUtil.randomCode(8),
                        copyStep.getStepInfo(), copyStep.getStatus());
                newStepId = stepDetailRepository.saveStep(newStep);
                newStep.setStepId(newStepId);
                // 获取原场景的执行状态
                SceneStepRel orgSceneStepRel = sceneStepRepository.queryByStepId(stepId);
                if (orgSceneStepRel == null) {
                    throw new AutoTestException("请复制正确的步骤");
                }
                // 绑定关联关系
                sceneId = orgSceneStepRel.getSceneId();
                SceneStepRel newSceneStepRel = SceneStepRel.build(sceneId, newStep);
                newSceneStepRel.setStatus(orgSceneStepRel.getStatus());
                sceneStepRepository.saveSceneStep(newSceneStepRel);
                // 更新场景的执行顺序
                SceneStepOrder sceneStepOrder = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
                List<Long> newStepIds;
                if (sceneStepOrder == null) {
                    // 新增执行顺序
                    newStepIds = new ArrayList<Long>(){{add(newStepId);}};
                } else {
                    // 更新执行顺序
                    List<Long> orderList = sceneStepOrder.getOrderList();
                    // 默认复制在后面
                    orderList.add(orderList.indexOf(stepId)+1, newStepId);
                    newStepIds = orderList;
                }
                stepOrderService.updateStepOrder(sceneId, newStepIds);
            }
            return newStepId;
        } catch (Exception e) {
            log.error("[CopyServiceImpl:stepCopy] copy step {} error, reason",
                    stepId, e);
            throw new AutoTestException(e.getMessage());
        }
    }



    @Deprecated
    public Long oldStepCopy(Long sceneId, Long stepId) {
        log.info("[CopyServiceImpl:oldStepCopy] copy step in sceneId {} with stepId {}", sceneId, stepId);
        try {
            // TODO: 2022/11/11 其实可以复制StepDetailService中的saveStepDetail 方法，只是会默认把复制的步骤放到最后 使用switch进行区分
            // 复制步骤
            Step copyStep = stepDetailRepository.queryStepById(stepId);
            if (copyStep == null) {
                throw new AutoTestException("当前被复制的步骤不存在");
            }
            Step newStep = new Step(null, copyStep.getStepName() + RandomUtil.randomCode(8),
                    copyStep.getStepInfo(), copyStep.getStatus());
            Long newStepId = stepDetailRepository.saveStep(newStep);
            copyStep.setStepId(newStepId);
            // 获取原场景的执行状态
            SceneStepRel orgSceneStepRel = sceneStepRepository.queryByStepId(stepId);
            if (orgSceneStepRel == null) {
                throw new AutoTestException("请复制正确的步骤");
            }
            if (sceneId == null) {
                sceneId = orgSceneStepRel.getSceneId();
            }
            SceneStepRel newSceneStepRel = SceneStepRel.build(sceneId, newStep);
            newSceneStepRel.setStatus(orgSceneStepRel.getStatus());
            // 绑定关联关系
            sceneStepRepository.saveSceneStep(newSceneStepRel);
            // 更新场景的执行顺序
            List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
            sceneStepOrders = sceneStepOrders.stream().filter(sceneStepOrder -> sceneStepOrder.getType().equals(
                    StepOrderEnum.BEFORE.getType()
            )).collect(Collectors.toList());
            List<Long> newStepIds;
            if (sceneStepOrders.isEmpty()) {
                // 新增执行顺序
                newStepIds = new ArrayList<>();
                newStepIds.add(newStepId);
            } else {
                // 更新执行顺序
                SceneStepOrder newSceneStepOrder = sceneStepOrders.get(0);
                List<Long> orderList = newSceneStepOrder.getOrderList();
                int index = orderList.indexOf(stepId);
                // 默认复制在后面
                orderList.add(index+1, newStepId);
                newStepIds = orderList;
            }
            stepOrderService.updateStepOrder(sceneId, newStepIds);
            return newStepId;
        } catch (Exception e) {
            log.error("[CopyServiceImpl:stepCopy] copy step {} error, reason",
                    stepId, e);
            throw new AutoTestException(e.getMessage());
        }
    }
}
