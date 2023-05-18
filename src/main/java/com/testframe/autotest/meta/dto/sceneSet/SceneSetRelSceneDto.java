package com.testframe.autotest.meta.dto.sceneSet;

import lombok.Data;

/**
 * 执行集关联场景
 */
@Data
public class SceneSetRelSceneDto {

    public Long sceneId;

    public Long setId;

    public Integer status;

    public Integer sort;

}
