package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.StepDetail;
import com.testframe.autotest.core.repository.mapper.StepDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepDetailDao {

    @Autowired
    private StepDetailMapper stepDetailMapper;

    public Long saveStepDetail(StepDetail stepDetail) {
        Long currentTime = System.currentTimeMillis();
        stepDetail.setCreateTime(currentTime);
        stepDetail.setUpdateTime(currentTime);
        if (stepDetailMapper.insert(stepDetail) > 0) {
            return stepDetail.getId();
        }
        return null;
    }

    public Boolean updateStepDetail(StepDetail stepDetail) {
        Long currentTime = System.currentTimeMillis();
        stepDetail.setUpdateTime(currentTime);
        if (stepDetailMapper.updateByPrimaryKey(stepDetail) > 0) {
            return true;
        }
        return false;

    }

    public StepDetail queryByStepId(Long stepId) {
        StepDetail stepDetail = stepDetailMapper.selectByPrimaryKey(stepId);
        return stepDetail;
    }


}
