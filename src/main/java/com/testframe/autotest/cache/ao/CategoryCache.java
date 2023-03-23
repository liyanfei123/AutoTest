package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.CategoryCacheKeys;
import com.testframe.autotest.cache.key.SceneStepRelCacheKeys;
import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CategoryCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 一级类目
    public HashMap<Integer, CategoryDetailCo> getFirstCategory() {
        String key = CategoryCacheKeys.genFirstCategoryKey();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                Map<Object,Object> map = stringRedisTemplate.opsForHash().entries(key);
                HashMap<Integer, CategoryDetailCo> categoryDetailCoMap = new HashMap<>();
                for (Object categoryId : map.keySet()) {
                    Object value = map.get(categoryId);
                    if (value == null) {
                        continue;
                    }
                    categoryDetailCoMap.put(Integer.valueOf(categoryId.toString()),
                            JSON.parseObject(value.toString(), CategoryDetailCo.class));
                }
                return categoryDetailCoMap;
            } catch (Exception ex) {
                log.error("[CategoryCache:getFirstCategory] catch-exception, key = {}, ex = {}", key, ex);
            }
        }
        return null;
    }

    public void batchUpdateFirstCategory(Collection<CategoryDetailCo> categoryDetailCos) {
        for (CategoryDetailCo categoryDetailCo : categoryDetailCos) {
            updateFirstCategory(categoryDetailCo);
        }
    }

    public void updateFirstCategory(CategoryDetailCo categoryDetailCo) {
        try {
            String key = CategoryCacheKeys.genFirstCategoryKey();
            if (categoryDetailCo != null) {
                stringRedisTemplate.opsForHash().put(key, categoryDetailCo.getCategoryId().toString(),
                        JSON.toJSONString(categoryDetailCo));
//                stringRedisTemplate.expire(key, CategoryCacheKeys.EXPIRATION_TIME_FIRST_CATEGORY, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            log.error("[SceneStepRelCache:updateFirstCategory] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(categoryDetailCo), ex);
        }
    }

    public void clearFirstCategory(Integer categoryId) {
        String key = CategoryCacheKeys.genFirstCategoryKey();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            if (categoryId != null) {
                stringRedisTemplate.opsForHash().delete(key, categoryId.toString());
            } else {
                stringRedisTemplate.opsForHash().delete(key);
            }
        }
    }

    // 多级子类目
    public HashMap<Integer, CategoryDetailCo> getLevelCategory(Integer categoryId) {
        String key = CategoryCacheKeys.genLevelCategoryKey(categoryId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                Map<Object,Object> map = stringRedisTemplate.opsForHash().entries(key);
                HashMap<Integer, CategoryDetailCo> categoryDetailCoMap = new HashMap<>();
                for (Object sonCategoryId : map.keySet()) {
                    Object value = map.get(sonCategoryId);
                    if (value == null) {
                        continue;
                    }
                    categoryDetailCoMap.put(Integer.valueOf(sonCategoryId.toString()),
                            JSON.parseObject(value.toString(), CategoryDetailCo.class));
                }
                return categoryDetailCoMap;
            } catch (Exception ex) {
                log.error("[CategoryCache:getFirstCategory] catch-exception, key = {}, ex = {}", key, ex);
            }
        }
        return null;
    }

    public void batchUpdateLevelCategory(Integer categoryId, Collection<CategoryDetailCo> categoryDetailCos) {
        for (CategoryDetailCo categoryDetailCo : categoryDetailCos) {
            updateLevelCategory(categoryId, categoryDetailCo);
        }
    }
    public void updateLevelCategory(Integer categoryId, CategoryDetailCo categoryDetailCo) {
        try {
            String key = CategoryCacheKeys.genLevelCategoryKey(categoryId);
            if (categoryDetailCo != null) {
                stringRedisTemplate.opsForHash().put(key, categoryDetailCo.getCategoryId().toString(),
                        JSON.toJSONString(categoryDetailCo));
//                stringRedisTemplate.expire(key, CategoryCacheKeys.EXPIRATION_TIME_LEVEL_CATEGORY, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            log.error("[SceneStepRelCache:updateFirstCategory] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(categoryDetailCo), ex);
        }
    }

    public void clearLevelCategory(Integer categoryId, Integer sonCategoryId) {
        String key = CategoryCacheKeys.genLevelCategoryKey(categoryId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            if (sonCategoryId != null) {
                stringRedisTemplate.opsForHash().delete(key, sonCategoryId.toString());
            } else {
                stringRedisTemplate.opsForHash().delete(key);
            }
        }
    }

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
