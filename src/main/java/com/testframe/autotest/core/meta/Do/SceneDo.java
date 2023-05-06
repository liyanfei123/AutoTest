package com.testframe.autotest.core.meta.Do;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class SceneDo {

    // 场景信息
    private SceneDetailDo sceneDetailDo;

    // 场景类目
    private CategorySceneDo categorySceneDo;

    private Integer oldCategoryId;

}
