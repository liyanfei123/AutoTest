package com.testframe.autotest.core.repository;


import com.testframe.autotest.core.meta.convertor.StepExecuteRecordConverter;
import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.core.repository.dao.StepExecuteRecordDao;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StepExecuteRecordRepository {

    @Autowired
    private StepExecuteRecordDao stepExecuteRecordDao;

    @Autowired
    private StepExecuteRecordConverter stepExecuteRecordConverter;

    @Transactional(rollbackFor = Exception.class)
    public Long saveStepExecuteRecord(StepExecuteRecord stepExecuteRecord) {
        StepRecord stepRecord = stepExecuteRecordConverter.toPo(stepExecuteRecord);
        return stepExecuteRecordDao.saveStepExecuteRecord(stepRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveStepExecuteRecord(List<StepExecuteRecord> stepExecuteRecords) {
        List<StepRecord> stepRecords = stepExecuteRecords.stream().map(stepExecuteRecordConverter::toPo)
                .collect(Collectors.toList());
        return stepExecuteRecordDao.batchSaveStepExecuteRecord(stepRecords);
    }

    public List<StepExecuteRecord> queryStepExecuteRecordByRecordId(Long recordId) {
        List<StepRecord> stepRecords = stepExecuteRecordDao.getStepRecordsByRecordId(recordId);
        List<StepExecuteRecord> stepExecuteRecords = stepRecords.stream().map(stepExecuteRecordConverter::toStepRecord)
                .collect(Collectors.toList());
        return stepExecuteRecords;
    }

    public HashMap<Long, List<StepExecuteRecord>> batchQueryStepExeRecord(List<Long> recordIds) {
        HashMap<Long, List<StepExecuteRecord>> stepExecuteRecordHashMap = new HashMap<>();
        for (Long recordId : recordIds) {
            List<StepExecuteRecord> stepExecuteRecords = queryStepExecuteRecordByRecordId(recordId);
            stepExecuteRecordHashMap.put(recordId, stepExecuteRecords);
        }
        return stepExecuteRecordHashMap;
    }

}
