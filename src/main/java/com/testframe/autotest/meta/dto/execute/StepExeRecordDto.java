package com.testframe.autotest.meta.dto.execute;

import com.testframe.autotest.meta.bo.StepExecuteRecord;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/04 22:43
 * @author: lyf
 */
@Data
public class StepExeRecordDto {

    // 步骤id 调试用
    private Long stepId;

    // 子场景步骤id
    private Long sceneRecordId;

    // 步骤名称
    private String stepName;

    //执行状态
    private Integer status;

    // 错误信息
    private String errInfo;

    public static StepExeRecordDto build(StepExecuteRecord stepExecuteRecord) {
        StepExeRecordDto stepExeRecordDto = new StepExeRecordDto();
        stepExeRecordDto.setStepId(stepExecuteRecord.getStepId());
        stepExeRecordDto.setSceneRecordId(stepExecuteRecord.getSceneRecordId());
        stepExeRecordDto.setStepName(stepExecuteRecord.getStepName());
        stepExeRecordDto.setStatus(stepExecuteRecord.getStatus());
        stepExeRecordDto.setErrInfo(stepExecuteRecord.getReason());
        return stepExeRecordDto;
    }

}
