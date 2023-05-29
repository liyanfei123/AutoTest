package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.SceneStepRelCacheKeys;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SceneStepRelCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public HashMap<Long, StepDetailDto> getSceneStepRels(Long sceneId) {
        String key = SceneStepRelCacheKeys.genStepInScene(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                Map<Object,Object> map = stringRedisTemplate.opsForHash().entries(key);
                HashMap<Long, StepDetailDto> stepDetailDtoMap = new HashMap<>();
                for (Object stepId : map.keySet()) {
                    Object value = map.get(stepId);
                    if (value == null) {
                        continue;
                    }
                    stepDetailDtoMap.put(Long.valueOf(stepId.toString()),
                            JSON.parseObject(value.toString(), StepDetailDto.class));
                }
                return stepDetailDtoMap;
            } catch (Exception ex) {
                log.error("[SceneStepRelCache:getSceneStepRels] catch-exception, key = {}, ex = {}",
                        key, ex);
            }
        }
        return null;
    }

    public void updateSceneStepRel(Long sceneId, Long stepId, StepDetailDto stepDetailDto) {
        try {
            String key = SceneStepRelCacheKeys.genStepInScene(sceneId);
            if (stepDetailDto != null) {
                stringRedisTemplate.opsForHash().put(key, stepId.toString(), JSON.toJSONString(stepDetailDto));
//                stringRedisTemplate.expire(key, SceneStepRelCacheKeys.EXPIRATION_TIME_STEP_SCENE, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            log.error("[SceneStepRelCache:updateSceneStepRel] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(stepDetailDto), ex);
        }
    }

    public void updateSceneStepRels(Long sceneId, Map<Long, StepDetailDto> stepDetailDtoMap) {
        try {
            String key = SceneStepRelCacheKeys.genStepInScene(sceneId);
            if (stepDetailDtoMap != null) {
                Map<String, String> newStepDetailDtoMap = new HashMap<>();
                for (Long stepId : stepDetailDtoMap.keySet()) {
                    newStepDetailDtoMap.put(stepId.toString(), JSON.toJSONString(stepDetailDtoMap.get(stepId)));
                }
                stringRedisTemplate.opsForHash().putAll(key, newStepDetailDtoMap);
//                stringRedisTemplate.expire(key, SceneStepRelCacheKeys.EXPIRATION_TIME_STEP_SCENE, TimeUnit.MILLISECONDS);
            }
        }  catch (Exception ex) {
            log.error("[SceneStepRelCache:updateSceneStepRels] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(stepDetailDtoMap), ex);
        }
    }

    public void clearSceneStepRel(Long sceneId, Long stepId) {
        String key = SceneStepRelCacheKeys.genStepInScene(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.opsForHash().delete(key, stepId);
        }
    }

    public void clearSceneStepRels(Long sceneId) {
        String key = SceneStepRelCacheKeys.genStepInScene(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.opsForHash().delete(key);
        }
    }

}
