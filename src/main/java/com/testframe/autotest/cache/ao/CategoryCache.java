package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.CategoryCacheKeys;
import com.testframe.autotest.cache.key.SceneDetailCacheKeys;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CategoryCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<CategorySceneDo> getSceneInCategory(Integer categoryId, Long start, Long end) {
        String key = CategoryCacheKeys.genSceneInCategoryKey(categoryId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                Set set = stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
                List<CategorySceneDo> categorySceneDos = new ArrayList<CategorySceneDo>(set);
            } catch (Exception ex) {
                log.error("[CategoryCache:getSceneInCategory] catch-exception, key = {}, ex = {}", key, ex);
            }
        }
        return null;
    }

    public void updateSceneToCategory(Integer categoryId, CategorySceneDo categorySceneDo) {
        String key = CategoryCacheKeys.genSceneInCategoryKey(categoryId);
        try {
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(categorySceneDo.getSceneId()),
                    categorySceneDo.getCreateTime());
            stringRedisTemplate.expire(key, CategoryCacheKeys.EXPIRATION_TIME_SCENE_CATEGORY, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("[CategoryCache:addSceneToCategory] catch-exception, add scene-category = {}, ex = {}",
                    JSON.toJSONString(categorySceneDo), ex);
        }
    }

    public void updateSceneInCategorys(Integer categoryId, List<CategorySceneDo> categorySceneDos) {
        String key = CategoryCacheKeys.genSceneInCategoryKey(categoryId);
        try {
            for (CategorySceneDo categorySceneDo : categorySceneDos) {
                stringRedisTemplate.opsForZSet().add(key, String.valueOf(categorySceneDo.getSceneId()),
                        categorySceneDo.getCreateTime());
            }
            stringRedisTemplate.expire(key, CategoryCacheKeys.EXPIRATION_TIME_SCENE_CATEGORY, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("[CategoryCache:updateSceneInCategory] catch-exception, update scene-category = {}, ex = {}",
                    JSON.toJSONString(categorySceneDos), ex);
        }
    }

    public void delSceneInCategory(Integer categoryId, Long sceneId) {
        String key = CategoryCacheKeys.genSceneInCategoryKey(categoryId);
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                stringRedisTemplate.opsForZSet().remove(key, String.valueOf(sceneId));
            }
        } catch (Exception ex) {
            log.error("[CategoryCache:addSceneToCategory] catch-exception, del scene-category = {}-{}, ex = {}",
                    sceneId, categoryId, ex);
        }
    }
}
