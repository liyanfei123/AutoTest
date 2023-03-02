package com.testframe.autotest.core.repository;


import com.testframe.autotest.core.meta.Do.StepExecuteRecordDo;
import com.testframe.autotest.core.meta.convertor.StepExecuteRecordConverter;
import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.core.repository.dao.StepExecuteRecordDao;
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
    public Long saveStepExecuteRecord(StepExecuteRecordDo stepExecuteRecordDo) {
        StepRecord stepRecord = stepExecuteRecordConverter.DoToPo(stepExecuteRecordDo);
        return stepExecuteRecordDao.saveStepExecuteRecord(stepRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveStepExecuteRecord(List<StepExecuteRecordDo> stepExecuteRecordDos) {
        List<StepRecord> stepRecords = stepExecuteRecordDos.stream().map(stepExecuteRecordConverter::DoToPo)
                .collect(Collectors.toList());
        return stepExecuteRecordDao.batchSaveStepExecuteRecord(stepRecords);
    }

    public List<StepExecuteRecordDo> queryStepExecuteRecordByRecordId(Long recordId) {
        List<StepRecord> stepRecords = stepExecuteRecordDao.getStepRecordsByRecordId(recordId);
        List<StepExecuteRecordDo> stepExecuteRecords = stepRecords.stream().map(stepExecuteRecordConverter::PoToDo)
                .collect(Collectors.toList());
        return stepExecuteRecords;
    }

    public HashMap<Long, List<StepExecuteRecordDo>> batchQueryStepExeRecord(List<Long> recordIds) {
        HashMap<Long, List<StepExecuteRecordDo>> stepExecuteRecordHashMap = new HashMap<>();
        for (Long recordId : recordIds) {
            List<StepExecuteRecordDo> stepExecuteRecords = queryStepExecuteRecordByRecordId(recordId);
            stepExecuteRecordHashMap.put(recordId, stepExecuteRecords);
        }
        return stepExecuteRecordHashMap;
    }

}
