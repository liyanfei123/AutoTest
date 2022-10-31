package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.convertor.StepDetailConvertor;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.core.meta.po.StepDetail;
import com.testframe.autotest.core.repository.dao.StepDetailDao;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.bo.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StepDetailRepository {

    @Autowired
    private StepDetailDao stepDetailDao;

    @Autowired
    private StepDetailConvertor stepDetailConvertor;

    @Transactional(rollbackFor = Exception.class)
    public Long saveStep(Step step) {
        StepDetail stepDetail = stepDetailConvertor.toPo(step);
        if (stepDetailDao.saveStepDetail(stepDetail) <= 0) {
            return null;
        }
        return stepDetail.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchSaveStep(List<Step> steps) {
        List<Long> stepIds = new ArrayList<>();
        List<StepDetail> stepDetails = steps.stream().map(stepDetailConvertor::toPo).collect(Collectors.toList());
        try {
            for (StepDetail sceneStep : stepDetails) {
                stepDetailDao.saveStepDetail(sceneStep);
                stepIds.add(sceneStep.getId());
            }
            return stepIds;
        } catch (Exception e) {
            log.error("[SceneStepRepository:batchSaveSceneStep] save scene-steps error, reason ={}", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Long update(Step step) {
        StepDetail stepDetail = stepDetailConvertor.toPo(step);
        return stepDetailDao.updateStepDetail(stepDetail);
    }

    public Step queryStepById(Long stepId) {
        StepDetail stepDetail = stepDetailDao.queryByStepId(stepId);
        if (stepDetail == null) {
            return null;
        } else {
            return build(stepDetail);
        }
    }

    public List<Step> queryStepByIds(List<Long> stepIds) {
        List<StepDetail> stepDetails = stepDetailDao.queryByStepIds(stepIds);
        if (CollectionUtils.isEmpty(stepDetails)) {
            return Collections.EMPTY_LIST;
        }
        List<Step> steps = stepDetails.stream().map(this::build).collect(Collectors.toList());
        return steps;
    }

    private Step build(StepDetail stepDetail) {
        if (stepDetail == null) {
            return null;
        }
        Step step = new Step();
        step.setStepId(stepDetail.getId());
        step.setStepName(stepDetail.getStepName());
        step.setStepInfo(stepDetail.getStepInfo());
        return step;
    }
}
