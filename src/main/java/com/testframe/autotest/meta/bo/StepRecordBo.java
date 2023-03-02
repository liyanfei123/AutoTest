package com.testframe.autotest.meta.bo;

import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;
import lombok.Data;

import java.util.List;

@Data
public class StepRecordBo {

    // 步骤类型，单步骤，子场景
    private Integer type;

    private Integer status;

    private StepExecuteRecordDto stepExecuteRecordDto;

    private SceneRecordBo sceneRecordBo;

}
