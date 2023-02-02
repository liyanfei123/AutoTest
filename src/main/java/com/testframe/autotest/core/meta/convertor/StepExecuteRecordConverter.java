package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @date:2022/10/21 21:10
 * @author: lyf
 */
@Component
public class StepExecuteRecordConverter {

    public StepRecord toPo(StepExecuteRecord stepExecuteRecord) {
        StepRecord stepRecord = new StepRecord();
        stepRecord.setRecordId(stepExecuteRecord.getRecordId());
        stepRecord.setStepId(stepExecuteRecord.getStepId());
        stepRecord.setSceneRecordId(stepExecuteRecord.getSceneRecordId());
        stepRecord.setStepName(stepExecuteRecord.getStepName());
        stepRecord.setReason(stepExecuteRecord.getReason());
        stepRecord.setStatus(stepExecuteRecord.getStatus());
        return stepRecord;
    }

    public StepExecuteRecord toStepRecord(StepRecord stepRecord) {
        StepExecuteRecord stepExecuteRecord = new StepExecuteRecord();
        stepExecuteRecord.setRecordId(stepRecord.getRecordId());
        stepExecuteRecord.setStepId(stepRecord.getStepId());
        stepExecuteRecord.setSceneRecordId(stepRecord.getSceneRecordId());
        stepExecuteRecord.setStepName(stepRecord.getStepName());
        stepExecuteRecord.setReason(stepRecord.getReason());
        stepExecuteRecord.setStatus(stepRecord.getStatus());
        return stepExecuteRecord;
    }



}
