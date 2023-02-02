package com.testframe.autotest.meta.dto.execute;


import lombok.Data;

import java.util.List;

@Data
public class SceneStepExeRecordDto {

    // 场景执行状态
    private Integer status;

    // 单场景下的步骤执行信息
    private List<StepExeRecordDto> sceneStepExeRecordDtos;

}
