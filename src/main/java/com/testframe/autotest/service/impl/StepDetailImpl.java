package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.meta.dto.StepInfoDto;
import com.testframe.autotest.meta.model.StepInfoModel;
import com.testframe.autotest.service.StepDetailService;
import com.testframe.autotest.service.StepOrderService;
import com.testframe.autotest.validator.StepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class StepDetailImpl implements StepDetailService {

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private StepOrderService stepOrderService;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveStepDetail(StepUpdateCmd stepUpdateCmd) {
        log.info("[StepDetailImpl:saveStepDetail] create/update step, stepUpdateCmd={}", JSON.toJSONString(stepUpdateCmd));
        try {
            // 检验参数的有效性
            stepValidator.checkStepUpdate(stepUpdateCmd);
            Step step = StepUpdateCmd.toStep(stepUpdateCmd);
            if (stepUpdateCmd.getStepId() == null) {
                // 新增
                Long stepId = stepDetailRepository.saveStep(step);
                SceneStepRel sceneStepRel = SceneStepRel.build(stepUpdateCmd.getSceneId(), step);
                sceneStepRel.setStepId(stepId);
                sceneStepRepository.saveSceneStep(sceneStepRel);
                // 新增步骤顺序
                stepOrderService.updateStepOrder(stepUpdateCmd.getSceneId(), stepId);
                return stepId;
            } else {
                // 更新
                // 判断当前场景是否还存在
                if (sceneDetailRepository.querySceneById(stepUpdateCmd.getSceneId()) == null) {
                    throw new AutoTestException("当前场景已被删除，无法修改");
                }
                stepDetailRepository.update(step);
                SceneStepRel sceneStepRel = sceneStepRepository.queryByStepId(stepUpdateCmd.getStepId());
                if (sceneStepRel.getStatus() != stepUpdateCmd.getStatus()) {
                    sceneStepRel.setStatus(stepUpdateCmd.getStatus());
                    sceneStepRepository.updateSceneStep(sceneStepRel);
                }
                return stepUpdateCmd.getStepId();
            }
        } catch (AutoTestException e) {
            log.error("[StepDetailImpl:saveStepDetail] create step, reason = {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Long> batchSaveStepDetail(List<StepUpdateCmd> stepUpdateCmds) {
        log.info("[StepDetailImpl:batchSaveStepDetail] batch update steps, count = {}", stepUpdateCmds.stream().count());
        Long sceneId = stepUpdateCmds.get(0).getSceneId();
        List<Long> stepIds = new ArrayList<>();
        try {
            List<Step> steps = new ArrayList<>();
            stepUpdateCmds.forEach(stepUpdateCmd -> {
                stepValidator.checkStepUpdate(stepUpdateCmd);
                Step step = StepUpdateCmd.toStep(stepUpdateCmd);
                steps.add(step);
            });
            if (sceneId == null) {
                for (Step step : steps) {
                    Long stepId = stepDetailRepository.saveStep(step);
                    stepIds.add(stepId);
                }
            } else {
                // 判断当前场景是否还存在
                if (sceneDetailRepository.querySceneById(sceneId) == null) {
                    throw new AutoTestException("当前场景已被删除，无法修改");
                }
                for (Step step : steps) {
                    stepIds.add(sceneId);
                    stepDetailRepository.update(step);
                }
            }
        } catch (AutoTestException e) {
            log.error("[StepDetailImpl:batchSaveStepDetail] create step, reason = {}", e.getMessage());
            return null;
        }
        return stepIds;
    }

    @Override
    public HashMap<Long, StepInfoDto> batchQueryStepDetail(List<Long> stepIds) {
       try {
           log.info("[StepDetailImpl:batchQueryStepDetail] query step detail ids: {}", JSON.toJSONString(stepIds));
           List<Step> steps = stepDetailRepository.queryStepByIds(stepIds);
           HashMap<Long, StepInfoDto> stepInfoDtoMap = new HashMap<>();
           for (Step step : steps) {
               StepInfoModel stepInfoModel;
               StepInfoDto stepInfoDto;
               try {
                   SceneStepRel sceneStepRel = sceneStepRepository.queryByStepId(step.getStepId());
                   stepInfoModel = JSON.parseObject(step.getStepInfo(), StepInfoModel.class);
                   stepInfoDto = StepInfoDto.build(stepInfoModel);
                   stepInfoDto.setStepId(step.getStepId());
                   stepInfoDto.setStepName(step.getStepName());
                   stepInfoDto.setStepStatus(sceneStepRel.getStatus());
                   stepInfoDtoMap.put(step.getStepId(), stepInfoDto);
               } catch (Exception e) {
                   log.error("[StepDetailImpl:batchQueryStepDetail] step = {}, reason = ", JSON.toJSONString(step), e);
               }
           }
           return stepInfoDtoMap;
       } catch (Exception e) {
           log.error("[StepDetailImpl:batchQueryStepDetail] query steps {} error, reason = {}", JSON.toJSONString(stepIds), e.getStackTrace());
           throw new AutoTestException("批量查询场景失败");
       }
    }


}
