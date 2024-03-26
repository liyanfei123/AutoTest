package com.testframe.autotest.meta.dto.sceneSet;

import com.testframe.autotest.meta.model.SceneSetConfigModel;
import lombok.Data;

/**
 * 执行集关联场景
 */
@Data
public class SceneSetRelSceneDto extends BaseSetRelDto {

    public Long sceneId;

    public SceneSetConfigModel sceneSetConfigModel;

}
