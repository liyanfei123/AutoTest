package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.domain.step.StepDomain;
import com.testframe.autotest.domain.stepOrder.StepOrderDomain;
import com.testframe.autotest.meta.command.StepOrderUpdateCmd;
import com.testframe.autotest.meta.command.StepStatusUpdateCmd;
import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.meta.command.UpdateStepsCmd;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.dto.step.StepSaveAndUpdateDto;
import com.testframe.autotest.meta.dto.step.StepsDto;
import com.testframe.autotest.meta.validator.SceneValidator;
import com.testframe.autotest.meta.validator.StepValidator;
import com.testframe.autotest.service.StepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StepServiceImpl implements StepService {

    @Resource
    private AutoTestConfig autoTestConfig;

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private SceneValidator sceneValidator;

    @Autowired
    private SceneDomain sceneDomain;

    @Autowired
    private StepDomain stepDomain;

    @Autowired
    private StepOrderDomain stepOrderDomain;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;


    // 仅可更新步骤，不可更新执行顺序
    @Override
    public Boolean updateStepDetail(UpdateStepsCmd updateStepsCmd) {
        log.info("[StepDetailImpl:saveStepDetail] create/update step, stepUpdateCmd={}", JSON.toJSONString(updateStepsCmd));
        try {
            if (updateStepsCmd.getSceneId() == null || updateStepsCmd.getSceneId() == 0L) {
                throw new AutoTestException("请输入正确的场景id");
            }
            Long sceneId = updateStepsCmd.getSceneId();
            List<StepUpdateCmd> stepUpdateCmds = updateStepsCmd.getStepUpdateCmds();
            List<StepUpdateCmd> reSelf = stepUpdateCmds.stream().filter(
                    stepUpdateCmd -> stepUpdateCmd.getSonSceneId() == sceneId).collect(Collectors.toList());
            if (!reSelf.isEmpty()) {
                throw new AutoTestException("不可引用自己");
            }

            // 将步骤进行划分
            List<StepUpdateCmd> needUpdateSteps = stepUpdateCmds.stream().filter(
                    stepUpdateCmd -> stepUpdateCmd.getStepId() > 0).collect(Collectors.toList());
            List<StepUpdateCmd> needSaveSteps = stepUpdateCmds.stream().filter(
                    stepUpdateCmd -> stepUpdateCmd.getStepId() == 0 || stepUpdateCmd.getStepId() == null)
                    .collect(Collectors.toList());

            sceneValidator.sceneIsExist(sceneId);
            if (!needSaveSteps.isEmpty()) {
                stepValidator.checkStepSave(sceneId, needSaveSteps);
            }
            if (!needUpdateSteps.isEmpty()) {
                stepValidator.checkStepUpdate(sceneId, needUpdateSteps);
            }
            List<Long> nowStepIds = updateStepsCmd.getStepUpdateCmds().stream().map(StepUpdateCmd::getStepId)
                    .collect(Collectors.toList());
            if (!needUpdateSteps.isEmpty()) {
                stepValidator.checkStepUpdate(sceneId, needUpdateSteps);
                List<Long> updateStepIds = needUpdateSteps.stream().map(stepUpdateCmd -> stepUpdateCmd.getStepId())
                        .collect(Collectors.toList());
                if (!stepValidator.checkStepWithScene(updateStepIds, sceneId)) {
                    throw new AutoTestException("步骤id错误");
                }
            }


            StepsDto updateStepsDto = new StepsDto(); // 需要更新的步骤
            StepsDto saveStepsDto = new StepsDto(); // 需要新增的步骤
            updateStepsDto.setSceneId(sceneId);
            List<StepDetailDto> updateStepDetailDtos = new ArrayList<>();
            List<StepDetailDto> saveStepDetailDtos = new ArrayList<>();
            for (StepUpdateCmd stepUpdateCmd : updateStepsCmd.getStepUpdateCmds()) {
                StepDetailDto stepDetailDto = new StepDetailDto();
                stepDetailDto.setSceneId(sceneId);
                stepDetailDto.setStepId(stepUpdateCmd.getStepId());
                stepDetailDto.setSonSceneId(stepUpdateCmd.getSonSceneId());
                stepDetailDto.setStepName(stepUpdateCmd.getName());
                stepDetailDto.setStepStatus(stepUpdateCmd.getStatus());
                stepDetailDto.setType(stepUpdateCmd.getSonSceneId() > 0L ? StepTypeEnum.SCENE.getType() :
                        StepTypeEnum.STEP.getType());
                stepDetailDto.setStepUIInfo(stepUpdateCmd.getStepInfo());
                if (stepUpdateCmd.getStepId() > 0) {
                    // 判断是否需要更新
                    if (stepDomain.needUpdate(stepUpdateCmd.getStepId(), stepDetailDto)) {
                        updateStepDetailDtos.add(stepDetailDto);
                    }
                } else {
                    saveStepDetailDtos.add(stepDetailDto);
                }
            }
            updateStepsDto.setStepDetailDtos(updateStepDetailDtos);
            saveStepsDto.setStepDetailDtos(saveStepDetailDtos);
            log.info("updateStepsDto, saveStepsDto");
            if (saveStepDetailDtos.isEmpty() && !updateStepDetailDtos.isEmpty()) {
                // 仅需更新
                return stepDomain.updateSteps(updateStepsDto);
            } else if (!saveStepDetailDtos.isEmpty() && !updateStepDetailDtos.isEmpty()) {
                // 仅需新增
                return !stepDomain.saveSteps(saveStepsDto).isEmpty();
            } else {
                StepSaveAndUpdateDto stepSaveAndUpdateDto = new StepSaveAndUpdateDto();
                stepSaveAndUpdateDto.setSceneId(sceneId);
                stepSaveAndUpdateDto.setSaveStepDetailDtos(saveStepDetailDtos);
                stepSaveAndUpdateDto.setUpdateStepDetailDtos(updateStepDetailDtos);
                stepSaveAndUpdateDto.setNowStepOrder(nowStepIds);
                return stepDomain.updateAndSaveSteps(stepSaveAndUpdateDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public Boolean removeStep(Long sceneId, Long stepId) {
        if (sceneId == null || stepId == null || sceneId == 0 || stepId == 0) {
            throw new AutoTestException("请输入正确的值");
        }
        try {
            sceneValidator.sceneIsExist(sceneId);
            stepValidator.checkStepId(stepId);
            List<Long> stepIds = new ArrayList<>(Arrays.asList(stepId));
            return stepDomain.deleteSteps(sceneId, stepIds);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public List<Long> addStepDetail(UpdateStepsCmd updateStepsCmd) {
        log.info("[StepDetailImpl:saveStepDetail] save step, stepUpdateCmd={}", JSON.toJSONString(updateStepsCmd));
        try {
            Long sceneId = updateStepsCmd.getSceneId();
            List<StepUpdateCmd> stepUpdateCmds = updateStepsCmd.getStepUpdateCmds();
            if (stepUpdateCmds == null || stepUpdateCmds.isEmpty()) {
                return null;
            }
            List<StepUpdateCmd> reSelf = stepUpdateCmds.stream().filter(
                    stepUpdateCmd -> stepUpdateCmd.getSonSceneId() == sceneId).collect(Collectors.toList());
            if (!reSelf.isEmpty()) {
                throw new AutoTestException("不可引用自己");
            }
            sceneValidator.sceneIsExist(sceneId);
            stepValidator.checkStepSave(sceneId, stepUpdateCmds);
            StepsDto stepsDto = new StepsDto();
            stepsDto.setSceneId(sceneId);

            List<StepDetailDto> stepDetailDtos = new ArrayList<>();
            for (StepUpdateCmd stepUpdateCmd : stepUpdateCmds) {
                StepDetailDto stepDetailDto = new StepDetailDto();
                stepDetailDto.setSceneId(sceneId);
                stepDetailDto.setStepId(null);
                stepDetailDto.setSonSceneId(stepUpdateCmd.getSonSceneId());
                if (stepUpdateCmd.getSonSceneId() != null && stepUpdateCmd.getSonSceneId() > 0L) {
                    SceneDetailDo sceneDetailDo = stepValidator.checkStepIsSon(sceneId, stepUpdateCmd);
                    if (stepUpdateCmd.getName() == null || stepUpdateCmd.getName() == "") {
                        // 子场景的步骤名默认选用场景名
                        stepUpdateCmd.setName(sceneDetailDo.getSceneName());
                    }
                    stepDetailDto.setType(StepTypeEnum.SCENE.getType());
                    stepUpdateCmd.setStepInfo(null);
                } else {
                    stepDetailDto.setType(StepTypeEnum.STEP.getType());
                }
                stepDetailDto.setStepName(stepUpdateCmd.getName());
                stepDetailDto.setStepStatus(stepUpdateCmd.getStatus());
                stepDetailDto.setStepUIInfo(stepUpdateCmd.getStepInfo());
                stepDetailDtos.add(stepDetailDto);
            }
            stepsDto.setStepDetailDtos(stepDetailDtos);
            List<Long> stepIds = stepDomain.saveSteps(stepsDto);
            return stepIds;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public Long stepCopy(Long sceneId, Long stepId) {
        // 判断当前场景下的步骤数
        List<Long> orderList = stepOrderDomain.stepOrderList(sceneId, StepOrderEnum.BEFORE.getType());
        if (orderList.isEmpty()) {
            return 0L;
        }
        if (!orderList.contains(stepId)) {
            throw new AutoTestException("步骤id无效");
        }
        if (!autoTestConfig.getCopySwitch()) {
            // 被复制到最后面
            orderList.add(-1L);
        } else {
            // 紧贴着复制步骤
            orderList.add(orderList.indexOf(stepId)+1, -1L);
        }
        return stepDomain.copyStep(sceneId, stepId, orderList);
    }

    @Override
    public Boolean changeStepOrder(StepOrderUpdateCmd stepOrderUpdateCmd) {
        sceneValidator.sceneIsExist(stepOrderUpdateCmd.getSceneId());
        // 判断是否传入错误的步骤id
        List<Long> oldStepOrder = stepOrderDomain.stepOrderList(stepOrderUpdateCmd.getSceneId(),
                StepOrderEnum.BEFORE.getType());
        List<Long> newStepOrder = stepOrderUpdateCmd.getOrders();
        HashSet<Long> orderSet = new HashSet<>(newStepOrder);
        if (oldStepOrder.size() != newStepOrder.size() || orderSet.size() != oldStepOrder.size()) {
            throw new AutoTestException("请检查输入的步骤id");
        }
        List<Long> noStepIds = newStepOrder.stream().filter(stepId -> !oldStepOrder.contains(stepId))
                .collect(Collectors.toList()); // 输入的非法步骤id
        if (!noStepIds.isEmpty()) {
            throw new AutoTestException("请检查输入的步骤id");
        }
        if (oldStepOrder.toString().equals(newStepOrder.toString())) {
            return true;
        }
        return stepOrderDomain.changeOrder(stepOrderUpdateCmd.getSceneId(), stepOrderUpdateCmd.getOrders());
    }

    @Override
    public Boolean changeStepStatus(StepStatusUpdateCmd stepStatusUpdateCmd) {
        if (stepStatusUpdateCmd.getType() == null || stepStatusUpdateCmd.getType() > 2) {
            stepStatusUpdateCmd.setType(1);
        }
        if (stepStatusUpdateCmd.getStatus() == null) {
            stepStatusUpdateCmd.setStatus(StepStatusEnum.OPEN.getType());
        }
        stepValidator.checkStepStatus(stepStatusUpdateCmd);
        try {
            Long sceneId = stepStatusUpdateCmd.getSceneId();
            sceneValidator.sceneIsExist(sceneId);
            if (stepStatusUpdateCmd.getType()  == 1) {
                // 修改单个步骤状态
                SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepIdAndSceneId(
                        stepStatusUpdateCmd.getStepId(), stepStatusUpdateCmd.getSceneId());
                if (sceneStepRelDo == null) {
                    throw new AutoTestException("当前步骤不存在");
                }
                if (sceneStepRelDo.getStatus() == stepStatusUpdateCmd.getStatus()) {
                    return true;
                }
                sceneStepRelDo.setStatus(stepStatusUpdateCmd.getStatus());
                sceneStepRepository.updateSceneStep(sceneStepRelDo);
            } else if (stepStatusUpdateCmd.getType() == 2) {
                // 修改整个场景步骤状态
//                List<StepDetailDto> listStepInfo = stepDomain.listStepInfo(sceneId);
                List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(stepStatusUpdateCmd.getSceneId());
                List<Integer> allStatus = sceneStepRelDos.stream().map(sceneStepRelDo -> sceneStepRelDo.getStatus())
                        .collect(Collectors.toList());
                allStatus.remove(stepStatusUpdateCmd.getStatus());
                if (allStatus.isEmpty()) {
                    return true;
                }
                sceneStepRelDos.forEach(sceneStepRelDo -> {
                            sceneStepRelDo.setStatus(stepStatusUpdateCmd.getStatus());
                        });
                sceneStepRepository.batchUpdateSceneStep(sceneStepRelDos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException("更新步骤状态错误");
        }
        return true;
    }


    // 子场景额外处理请求输入参数
    private void stepIsScene(StepUpdateCmd stepUpdateCmd) {
        SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(stepUpdateCmd.getSonSceneId());
        stepUpdateCmd.setName(sceneDetailDo.getSceneName());
        stepUpdateCmd.setStepInfo(sceneDetailDo.getSceneDesc());
    }

    // TODO: 2023/3/1 增加场景嵌套关系判断
    // 子场景之间不可相互嵌套
    // 比如 场景1 已经关联了 场景2 作为子步骤，场景3 又关联了 场景1 作为子步骤，此时场景2是不可再关联场景3的
    // 可能会涉及多层嵌套
    // 或者直接步骤间最大允许嵌套两层

    // 查询场景2作为子场景的所有的父场景，判断当前父场景所关联的所有场景，不可再次关联


}
