package com.testframe.autotest.meta.vo;


import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;
import lombok.Data;

@Data
public class StepExeRecordInfo {

    // 步骤类型，单步骤，子场景
    private Integer type;

    private Integer status;

    // 单步骤执行记录信息
    private StepExecuteRecordDto stepExecuteRecordDto;

    // 子场景执行信息
    private SceneExeRecordVo sonSceneExeRecordVo;

}
