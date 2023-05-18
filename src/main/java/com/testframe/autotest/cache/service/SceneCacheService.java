package com.testframe.autotest.cache.service;

import com.testframe.autotest.meta.dto.scene.SceneDetailDto;

import java.util.HashMap;
import java.util.List;

public interface SceneCacheService {

    SceneDetailDto getSceneDetailFromCache(Long sceneId);

    HashMap<Long, SceneDetailDto> getSceneDetailsFromCache(List<Long> sceneIds);

}
