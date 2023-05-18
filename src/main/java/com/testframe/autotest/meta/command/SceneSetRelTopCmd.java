package com.testframe.autotest.meta.command;

import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.Data;

import java.util.List;

/**
 * 置顶
 */
@Data
public class SceneSetRelTopCmd {

    public Long setId;

    // status可不传
    public SceneSetRelSceneDto sceneSetRelSceneDto;

    // status可不传
    public SceneSetRelStepDto sceneSetRelStepDto;
}
