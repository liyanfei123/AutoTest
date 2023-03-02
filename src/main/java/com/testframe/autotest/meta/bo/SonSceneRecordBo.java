package com.testframe.autotest.meta.bo;

import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import lombok.Data;

import java.util.List;

@Data
public class SonSceneRecordBo {

    private SceneExecuteRecordDto sonSceneExeDto;

    private List<StepRecordBo> sonSceneStepExeDto;

}
