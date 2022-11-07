package com.testframe.autotest.meta.dto;

import lombok.Data;

import java.util.List;

@Data
public class SceneDetailInfo {

    private SceneInfoDto scene;

    private List<StepInfoDto> steps;

}
