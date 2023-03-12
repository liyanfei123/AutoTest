package com.testframe.autotest.cache.ao;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.key.StepDetailCacheKeys;
import com.testframe.autotest.core.meta.Do.StepDetailDo;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StepDetailCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public StepDetailDto getStepDetail(Long stepId) {
        String key = StepDetailCacheKeys.genStepDetailKey(stepId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            try {
                return JSON.parseObject(stringRedisTemplate.opsForValue().get(key), StepDetailDto.class);
            } catch (Exception ex) {
                log.error("[StepDetailCache:getStepDetail] catch-exception, key = {}, ex = {}",
                        key, ex);
            }
        }
        return null;
    }


    public void updateStepDetail(Long stepId, StepDetailDto stepDetailDto) {
        try {
            if (stepDetailDto != null) {
                String key = StepDetailCacheKeys.genStepDetailKey(stepId);
                stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(stepDetailDto));
                stringRedisTemplate.expire(key, StepDetailCacheKeys.EXPIRATION_TIME_STEP_DETAIL, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            log.error("[StepDetailCache:updateStepDetail] catch-exception, keyInfo = {}, ex = {}",
                    JSON.toJSONString(stepDetailDto), ex);
        }
    }


    /**
     * 失效缓存
     */
    public void clearStepDetailCache(Long stepId) {
        String key = StepDetailCacheKeys.genStepDetailKey(stepId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }
    }

    public void clearStepDetailCaches(List<Long> stepIds) {
        for (Long stepId : stepIds) {
            clearStepDetailCache(stepId);
        }
    }

}
