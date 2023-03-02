package com.testframe.autotest.domain.scene;

import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.scene.SceneSearchListDto;
import com.testframe.autotest.meta.query.SceneQry;

import java.util.List;

// 关于场景的业务封装逻辑
public interface SceneDomain {

    public Long updateScene(SceneDetailDto sceneDetailDto);

    SceneDetailDto getSceneById(Long sceneId);

    Boolean deleteScene(Long sceneId);

    SceneSearchListDto searchScene(SceneQry sceneQry);
}
