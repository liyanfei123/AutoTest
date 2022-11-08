package com.testframe.autotest.meta.dto.execute;

import com.testframe.autotest.meta.bo.StepExecuteRecord;
import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/04 22:43
 * @author: lyf
 */
@Data
public class StepExeRecordDto {

    // 步骤名称
    private String stepName;

    //执行状态
    private Integer status;

    // 错误信息
    private String errInfo;

    public static StepExeRecordDto build(StepExecuteRecord stepExecuteRecord) {
        StepExeRecordDto stepExeRecordDto = new StepExeRecordDto();
        stepExeRecordDto.setStepName(stepExecuteRecord.getStepName());
        stepExeRecordDto.setStatus(stepExecuteRecord.getStatus());
        stepExeRecordDto.setErrInfo(stepExecuteRecord.getReason());
        return stepExeRecordDto;
    }

}
