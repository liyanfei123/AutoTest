package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.SceneStepRelCacheKeys;
import com.testframe.autotest.cache.key.StepDetailCacheKeys;
import com.testframe.autotest.cache.key.StepOrderCacheKeys;
import com.testframe.autotest.core.meta.Do.StepDetailDo;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// 步骤执行顺序缓存
@Slf4j
@Component
public class StepOrderCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public StepOrderDo getBeforeStepOrder(Long sceneId) {
        String key = StepOrderCacheKeys.genBeforeStepOrderKey(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                return JSON.parseObject(stringRedisTemplate.opsForValue().get(key), StepOrderDo.class);
            } catch (Exception ex) {
                log.error("[StepOrderCache:getBeforeStepOrder] catch-exception, key = {}, ex = {}",
                        key, ex);
            }
        }
        return null;
    }

    public void updateBeforeStepOrder(Long sceneId, StepOrderDo stepOrderDo) {
        try {
            String key = StepOrderCacheKeys.genBeforeStepOrderKey(sceneId);
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(stepOrderDo));
            stringRedisTemplate.expire(key, StepOrderCacheKeys.EXPIRATION_TIME_BEFORE_STEP_ORDER, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("[StepOrderCache:updateBeforeStepOrder] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(stepOrderDo), ex);
        }
    }

    /**
     * 失效缓存
     */
    public void clearBeforeStepOrderCache(Long sceneId) {
        String key = StepOrderCacheKeys.genBeforeStepOrderKey(sceneId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }
    }

    // 删除指定位置的元素

}
