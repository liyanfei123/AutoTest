package com.testframe.autotest.meta.dto;

import lombok.Data;

import java.util.List;

@Data
public class SceneDetailInfo {

    private SceneInfoDto scene;

    // 已按执行顺序编排好
    private List<StepInfoDto> steps;

}
