package com.testframe.autotest.meta.dto;

import lombok.Data;

import java.util.List;

@Data
public class SceneDetailInfo {

    private SceneSimpleInfoDto scene;

    private List<StepInfoDto> steps;

}
