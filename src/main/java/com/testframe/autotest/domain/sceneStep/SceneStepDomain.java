package com.testframe.autotest.domain.sceneStep;

import com.testframe.autotest.meta.dto.scene.SceneDetailDto;

import java.util.List;
import java.util.Map;

public interface SceneStepDomain {

    Boolean sceneIncludeSelf(Long sceneId, Long sonSceneId);

    Map<Long, List<Long>> scenesInOther(List<Long> sonSceneIds);

    /**
     * 获得当前场景的所有父场景，只记录第一级
     * 若其父场景再次被引用，不被计入
     * @param sceneId
     * @return
     */
    List<SceneDetailDto> fatherScene(Long sceneId);

}
