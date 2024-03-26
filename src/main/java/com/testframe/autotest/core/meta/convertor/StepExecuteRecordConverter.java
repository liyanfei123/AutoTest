package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.Do.StepExecuteRecordDo;
import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @date:2022/10/21 21:10
 * @author: lyf
 */
@Component
public class StepExecuteRecordConverter {

    public StepRecord DoToPo(StepExecuteRecordDo stepExecuteRecordDo) {
        StepRecord stepRecord = new StepRecord();
        stepRecord.setId(stepExecuteRecordDo.getStepRecordId());
        stepRecord.setRecordId(stepExecuteRecordDo.getRecordId());
        stepRecord.setStepId(stepExecuteRecordDo.getStepId());
        stepRecord.setSceneRecordId(stepExecuteRecordDo.getSceneRecordId());
        stepRecord.setStepName(stepExecuteRecordDo.getStepName());
        stepRecord.setReason(stepExecuteRecordDo.getReason());
        stepRecord.setStatus(stepExecuteRecordDo.getStatus());
        return stepRecord;
    }

    public StepExecuteRecordDo PoToDo(StepRecord stepRecord) {
        StepExecuteRecordDo stepExecuteRecordDo = new StepExecuteRecordDo();
        stepExecuteRecordDo.setStepRecordId(stepRecord.getId());
        stepExecuteRecordDo.setRecordId(stepRecord.getRecordId());
        stepExecuteRecordDo.setStepId(stepRecord.getStepId());
        stepExecuteRecordDo.setSceneRecordId(stepRecord.getSceneRecordId());
        stepExecuteRecordDo.setStepName(stepRecord.getStepName());
        stepExecuteRecordDo.setReason(stepRecord.getReason());
        stepExecuteRecordDo.setStatus(stepRecord.getStatus());
        return stepExecuteRecordDo;
    }

    public StepExecuteRecordDo DtoToDo(StepExecuteRecordDto stepExecuteRecordDto) {
        StepExecuteRecordDo stepExecuteRecordDo = new StepExecuteRecordDo();
        stepExecuteRecordDo.setStepRecordId(null);
        stepExecuteRecordDo.setStepId(stepExecuteRecordDto.getStepId());
        stepExecuteRecordDo.setSceneRecordId(stepExecuteRecordDto.getSceneRecordId());
        stepExecuteRecordDo.setStepName(stepExecuteRecordDto.getStepName());
        stepExecuteRecordDo.setReason(stepExecuteRecordDto.getReason());
        stepExecuteRecordDo.setStatus(stepExecuteRecordDto.getStatus());
        return stepExecuteRecordDo;
    }

    public StepExecuteRecordDto DoToDto(StepExecuteRecordDo stepExecuteRecordDo) {
        StepExecuteRecordDto stepExecuteRecordDto = new StepExecuteRecordDto();
        stepExecuteRecordDto.setStepRecordId(stepExecuteRecordDo.getStepRecordId());
        stepExecuteRecordDto.setStepId(stepExecuteRecordDo.getStepId());
        stepExecuteRecordDto.setSceneRecordId(stepExecuteRecordDo.getSceneRecordId());
        stepExecuteRecordDto.setStepName(stepExecuteRecordDo.getStepName());
        stepExecuteRecordDto.setReason(stepExecuteRecordDo.getReason());
        stepExecuteRecordDto.setStatus(stepExecuteRecordDo.getStatus());
        return stepExecuteRecordDto;
    }

}
