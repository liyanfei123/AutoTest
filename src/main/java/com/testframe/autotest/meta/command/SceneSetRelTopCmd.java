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

    private Long setId;

    private Long sceneId;

    private Long stepId;

    private Integer sort;
//
//    // status可不传
//    public SceneSetRelSceneDto sceneSetRelSceneDto;
//
//    // status可不传
//    public SceneSetRelStepDto sceneSetRelStepDto;
}
