package com.testframe.autotest.domain.sceneStep;

import java.util.List;
import java.util.Map;

public interface SceneStepDomain {

    Boolean sceneIncludeSelf(Long sceneId, Long sonSceneId);

    Map<Long, List<Long>> scenesInOther(List<Long> sonSceneIds);

}
