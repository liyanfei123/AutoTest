package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.core.repository.mapper.StepRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepExecuteRecordDao {

    @Autowired
    private StepRecordMapper stepRecordMapper;

    public Boolean saveStepExecuteRecord(StepRecord stepRecord) {
        Long currentTime = System.currentTimeMillis();
        stepRecord.setCreateTime(currentTime);
        return stepRecordMapper.insert(stepRecord) > 0 ? true : false;
    }

    public StepRecord getStepRecordById(Long stepId) {
        return stepRecordMapper.selectByPrimaryKey(stepId);
    }
}
