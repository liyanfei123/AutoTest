package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.convertor.StepDetailConvertor;
import com.testframe.autotest.core.meta.po.StepDetail;
import com.testframe.autotest.core.repository.dao.StepDetailDao;
import com.testframe.autotest.meta.bo.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean update(Step step) {
        StepDetail stepDetail = stepDetailConvertor.toPo(step);
        return !stepDetailDao.updateStepDetail(stepDetail) ? false : true;
    }

    public Step queryStepById(Long stepId) {
        StepDetail stepDetail = stepDetailDao.queryByStepId(stepId);
        if (stepDetail == null) {
            return null;
        } else {
            Step step = new Step();
            step.setStepId(stepId);
            step.setStepName(stepDetail.getStepName());
            step.setStepInfo(stepDetail.getStepInfo());
            return step;
        }
    }
}
