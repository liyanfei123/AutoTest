package com.testframe.autotest.cache.key;

public class StepOrderCacheKeys {

    /**
     * String 场景执行前的顺序
     * @param sceneId
     * @return
     */
    public static String genBeforeStepOrderKey(Long sceneId) {
        return String.format("before_step_order_%s", sceneId);
    }

    public static final long EXPIRATION_TIME_BEFORE_STEP_ORDER = -1;


}
