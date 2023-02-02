package com.testframe.autotest.meta.dto.execute;


import lombok.Data;

import java.util.List;

@Data
public class StepExeRecordInfo {

    // 步骤类型，单步骤，子场景
    private Integer type;

    private Integer status;

    // 单步骤执行记录信息
    private StepExeRecordDto stepExeRecordDto;

    private SceneStepExeRecordDto sceneStepExeRecordDto;

}
