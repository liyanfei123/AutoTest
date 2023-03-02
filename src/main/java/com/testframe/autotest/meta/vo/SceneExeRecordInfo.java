package com.testframe.autotest.meta.vo;

import lombok.Data;

import java.util.List;

@Data
public class SceneExeRecordInfo {

    private SceneExeInfoVo sceneExeInfoVo;

    private List<StepExeRecordInfo> stepExeRecordInfos;
}
