package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.StepRunResultEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.service.StepRecordService;
import com.testframe.autotest.ui.meta.StepExeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StepRecordServiceImpl implements StepRecordService {

    @Autowired
    private StepExecuteRecordRepository stepExecuteRecordRepository;

    @Override
    public Long saveRecord(Long recordId, StepExeInfo stepExeInfo, Integer status, String reason) {
        try {
            StepExecuteRecord stepExecuteRecord = build(recordId, stepExeInfo, status, reason);
            log.info("[StepRecordServiceImpl:saveRecord] add step run record, data = {}",
                    JSON.toJSONString(stepExecuteRecord));
            return stepExecuteRecordRepository.saveStepExecuteRecord(stepExecuteRecord);
        } catch (Exception e) {
            log.error("[StepRecordServiceImpl:saveRecord] add step run record error, reason = {}", e);
            throw new AutoTestException("保存步骤执行信息错误");
        }
    }

    @Override
    public boolean batchSaveRecord(Long recordId, List<StepExeInfo> stepExeInfoList, Integer status, String reason) {
        try {
            List<StepExecuteRecord> stepExecuteRecords = new ArrayList<>();
            stepExeInfoList.forEach(stepExeInfo -> {
                StepExecuteRecord stepExecuteRecord = build(recordId, stepExeInfo, status, reason);
                stepExecuteRecords.add(stepExecuteRecord);
            });
            log.info("[StepRecordServiceImpl:batchSaveRecord] add step run records, data = {}",
                    JSON.toJSONString(stepExecuteRecords));
            return stepExecuteRecordRepository.batchSaveStepExecuteRecord(stepExecuteRecords);
        } catch (Exception e) {
            log.error("[StepRecordServiceImpl:batchSaveRecord] add step run records error, reason = {}", e);
            throw new AutoTestException("批量保存步骤执行信息错误");
        }
    }

    @Override
    public boolean batchSaveRecord(List<StepExecuteRecord> stepExecuteRecords) {
        try {
            log.info("[StepRecordServiceImpl:batchSaveRecord] add step run records, data = {}",
                    JSON.toJSONString(stepExecuteRecords));
            return stepExecuteRecordRepository.batchSaveStepExecuteRecord(stepExecuteRecords);
        } catch (Exception e) {
            log.error("[StepRecordServiceImpl:batchSaveRecord] add step run records error, reason = {}", e);
            throw new AutoTestException("批量保存步骤执行信息错误");
        }
    }

    private StepExecuteRecord build(Long recordId, StepExeInfo stepExeInfo, Integer status, String reason) {
        StepExecuteRecord stepExecuteRecord = new StepExecuteRecord();
        stepExecuteRecord.setRecordId(recordId);
        stepExecuteRecord.setStepId(stepExeInfo.getStepId());
        stepExecuteRecord.setStepName(stepExeInfo.getStepName());
        if (status == null) {
            status = StepRunResultEnum.CLOSE.getType();
        }
        stepExecuteRecord.setStatus(status);
        stepExecuteRecord.setReason(reason);
        return stepExecuteRecord;
    }


}
