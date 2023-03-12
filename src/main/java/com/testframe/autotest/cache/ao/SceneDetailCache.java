package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.SceneDetailCacheKeys;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SceneDetailCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 读缓存
     * @param sceneId
     * @return
     */
    public SceneDetailDto getSceneDetail(Long sceneId) {
        String key = SceneDetailCacheKeys.genSceneDetailKey(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                return JSON.parseObject(stringRedisTemplate.opsForValue().get(key), SceneDetailDto.class);
            } catch (Exception ex) {
                log.error("[SceneDetailCache:getSceneDetail] catch-exception, key = {}, ex = {}", key, ex);
            }
        }
        return null;
    }

    /**
     * 更新场景信息缓存
     * @param sceneId
     * @param sceneDetailDto
     */
    public void updateSceneDetail(Long sceneId, SceneDetailDto sceneDetailDto) {
        try {
            if (sceneDetailDto == null) {
                return;
            }
            String key = SceneDetailCacheKeys.genSceneDetailKey(sceneId);
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(sceneDetailDto));
            // 不设置时间就是永不失效
            stringRedisTemplate.expire(key, SceneDetailCacheKeys.EXPIRATION_TIME_SCENE_DETAIL, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("[SceneDetailCache:updateSceneDetail] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(sceneDetailDto), ex);
        }
    }

    public void clearSceneDetailCache(Long sceneId) {
        String key = SceneDetailCacheKeys.genSceneDetailKey(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * 获取总场景数
     * @return
     */
    public Long getSceneCount() {
        String key = SceneDetailCacheKeys.genSceneCount();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                return Long.valueOf(stringRedisTemplate.opsForValue().get(key));
            } catch (Exception ex) {
                log.error("[SceneDetailCache:getSceneCount] catch-exception, key = {}, ex = {}", key, ex);
            }
        }
        return null;
    }

    // TODO: 2023/3/12  增加刷新数量的定时调度job
    public Boolean incrCount(long i) {
        String key = SceneDetailCacheKeys.genSceneCount();
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                stringRedisTemplate.opsForValue().increment(key, i);
            }
        } catch (Exception ex) {
            log.error("[SceneDetailCache:incrCount] catch-exception, key = {}, ex = {}", key, ex);
        }
        return true;
    }

    public Boolean decrCount(long i) {
        String key = SceneDetailCacheKeys.genSceneCount();
        try {
            Long count = getSceneCount();
            if (count > 0) {
                stringRedisTemplate.opsForValue().decrement(key, i);
            }
        } catch (Exception ex) {
            log.error("[SceneDetailCache:incrCount] catch-exception, key = {}, ex = {}", key, ex);
        }
        return true;
    }

}
