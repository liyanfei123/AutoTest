package com.testframe.autotest.meta.dto.sceneSet;

import lombok.Data;

/**
 * 执行集关联步骤
 */
@Data
public class SceneSetRelStepDto {

    public Long stepId;

    public Long setId;

    public Integer status;

    public Integer sort;

}
