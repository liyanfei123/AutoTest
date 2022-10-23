package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.command.StepCreateCmd;
import com.testframe.autotest.command.StepUpdateCmd;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.service.StepDetailInter;
import com.testframe.autotest.validator.StepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StepDetailImpl implements StepDetailInter {

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Override
    public Long saveStepDetail(StepUpdateCmd stepUpdateCmd) {
        log.info("[StepDetailImpl:saveStepDetail] create/update step, stepUpdateCmd={}", JSON.toJSONString(stepUpdateCmd));
        try {
            // 检验参数的有效性
            stepValidator.checkStepUpdate(stepUpdateCmd);
            Step step = StepUpdateCmd.toStep(stepUpdateCmd);
            if (stepUpdateCmd.getStepId() == null) {
                return stepDetailRepository.saveStep(step);
            } else {
                // 判断当前场景是否还存在
                if (sceneDetailRepository.querySceneById(stepUpdateCmd.getSceneId()) == null) {
                    throw new AutoTestException("当前场景已被删除，无法修改");
                }
                return stepDetailRepository.update(step);
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


}
