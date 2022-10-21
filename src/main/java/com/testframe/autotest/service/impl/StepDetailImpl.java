package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.command.StepCreateCmd;
import com.testframe.autotest.command.StepUpdateCmd;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.service.StepDetailInter;
import com.testframe.autotest.validator.StepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StepDetailImpl implements StepDetailInter {

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Override
    public Boolean saveStepDetail(StepCreateCmd stepCreateCmd) {
        log.info("[StepDetailImpl:saveStepDetail] create step, stepCreateCmd={}", JSON.toJSONString(stepCreateCmd));
        try {
            stepValidator.checkStepCreate(stepCreateCmd);
        } catch (AutoTestException e) {
            log.error("[StepDetailImpl:saveStepDetail] create step, reason = {}", e.getMessage());
            return false;
        }
        return true;
    }


    private Step build(StepCreateCmd stepCreateCmd) {
        Step step = new Step();
        step.setStatus(StepStatusEnum.OPEN.getType());
        step.setStepName(stepCreateCmd.getStepName());
        step.setStepInfo(stepCreateCmd.getStepInfo());
        return step;
    }

    private Step update(StepUpdateCmd stepUpdateCmd) {
        Step step = new Step();
        step.setStepId(stepUpdateCmd.getStepId());
        step.setStepName(stepUpdateCmd.getName());
        step.setStepInfo(stepUpdateCmd.getStepInfo());
        step.setStatus(stepUpdateCmd.getStatus());
        return step;
    }
}
