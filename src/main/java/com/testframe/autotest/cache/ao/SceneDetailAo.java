package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.SceneDetailCacheKeys;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SceneDetailAo {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 读缓存
     * @param sceneId
     * @return
     */
    public SceneDetailDo getSceneDetail(Long sceneId) {
        String baseCacheKey = SceneDetailCacheKeys.genSceneDetailKey(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(baseCacheKey))) {
            try {
                return JSON.parseObject(stringRedisTemplate.opsForValue().get(baseCacheKey), SceneDetailDo.class);
            } catch (Exception ex) {
                log.error("[op:getBase] catch-exception key={}", baseCacheKey, ex);
            }
        }
        return null;
    }

    /**
     * 写缓存
     * @param sceneId
     * @param sceneDetailDo
     */
    public void writeSceneDetail(Long sceneId, SceneDetailDo sceneDetailDo) {
        try {
            sceneDetailDo = null == sceneDetailDo ? sceneDetailDo.NULL : sceneDetailDo;
            stringRedisTemplate.opsForValue().set(SceneDetailCacheKeys.genSceneDetailKey(sceneId), JSON.toJSONString(sceneDetailDo),
                    SceneDetailCacheKeys.EXPIRATION_TIME_SCENE_DETAIL, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("[op:writeBase] catch-exception scene detail = {}", JSON.toJSONString(sceneDetailDo), ex);
        }
    }

    /**
     * 失效缓存
     */
    public void clearCache(Long sceneId) {
        stringRedisTemplate.delete(SceneDetailCacheKeys.genSceneDetailKey(sceneId));
    }

}
