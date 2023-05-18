package com.testframe.autotest.meta.command;

import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.Data;

import java.util.List;

@Data
public class SceneSetRelCmd {

    public Long setId;

    public List<SceneSetRelSceneDto> sceneSetRelSceneDtos;

    public List<SceneSetRelStepDto> sceneSetRelStepDtos;
}
