package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.core.repository.mapper.StepRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StepExecuteRecordDao {

    @Autowired
    private StepRecordMapper stepRecordMapper;

    public Long saveStepExecuteRecord(StepRecord stepRecord) {
        Long currentTime = System.currentTimeMillis();
        stepRecord.setCreateTime(currentTime);
        stepRecordMapper.insertSelective(stepRecord);
        return stepRecord.getId();
    }

    public boolean batchSaveStepExecuteRecord(List<StepRecord> stepRecords) {
        Long currentTime = System.currentTimeMillis();
        stepRecords.forEach(stepRecord -> stepRecord.setCreateTime(currentTime));
        return stepRecordMapper.batchInsert(stepRecords) > 0 ? true : false;
    }

    public StepRecord getStepRecordsById(Long stepId) {
        return stepRecordMapper.selectByPrimaryKey(stepId);
    }

    public List<StepRecord> getStepRecordsByRecordId(Long recordId) {
        List<StepRecord> stepRecords = stepRecordMapper.getStepRecordByRecordId(recordId);
        if (CollectionUtils.isEmpty(stepRecords)) {
            return Collections.EMPTY_LIST;
        }
        return stepRecords;
    }
}
