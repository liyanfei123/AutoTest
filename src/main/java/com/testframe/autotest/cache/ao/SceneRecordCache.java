package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.SceneRecordCacheKeys;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// 场景执行记录简单缓存
@Slf4j
@Component
public class SceneRecordCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public SceneSimpleExecuteDto getSceneRecExe(Long sceneId) {
        String key = SceneRecordCacheKeys.genSceneRecentlyExe(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                return JSON.parseObject(stringRedisTemplate.opsForValue().get(key),
                        SceneSimpleExecuteDto.class);
            } catch (Exception ex) {
                log.error("[SceneRecordCache:updateSceneRecExe] catch-exception, key = {}, ex = {}",
                        key, ex);
            }
        }
        return null;
    }


    public void updateSceneRecExe(Long sceneId, SceneSimpleExecuteDto sceneSimpleExecuteDto) {
        try {
            if (sceneSimpleExecuteDto == null) {
                return;
            }
            String key = SceneRecordCacheKeys.genSceneRecentlyExe(sceneId);
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(sceneSimpleExecuteDto));
//            stringRedisTemplate.expire(key, SceneRecordCacheKeys.EXPIRATION_TIME_SCENE_RECENTLY_EXE, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("[SceneRecordCache:updateSceneRecExe] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(sceneSimpleExecuteDto), ex);
        }
    }

    public void clearSceneRecExeCache(Long sceneId) {
        String key = SceneRecordCacheKeys.genSceneRecentlyExe(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }
    }

}
