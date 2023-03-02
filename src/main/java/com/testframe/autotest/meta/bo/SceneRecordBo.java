package com.testframe.autotest.meta.bo;

import com.testframe.autotest.meta.bo.StepRecordBo;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import lombok.Data;

import java.util.List;

@Data
public class SceneRecordBo {

    // 场景执行信息
    private SceneExecuteRecordDto sceneExecuteRecordDto;

    // 步骤执行信息
    private List<StepRecordBo> stepRecordBos;

}
