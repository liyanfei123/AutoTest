package com.testframe.autotest.cache.service;

import com.testframe.autotest.meta.dto.step.StepDetailDto;

import java.util.HashMap;
import java.util.List;

public interface StepCacheService {

    /**
     * 获取场景下的所有步骤
     * 仅过滤是否删除，不过滤步骤状态
     * @param sceneId
     * @return
     */
    List<StepDetailDto> getStepInSceneFromCache(Long sceneId);

    StepDetailDto getStepInfoFromCache(Long stepId);

}
