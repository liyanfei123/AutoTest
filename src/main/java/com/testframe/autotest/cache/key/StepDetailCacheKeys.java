package com.testframe.autotest.cache.key;

public class StepDetailCacheKeys {

    /**
     * 步骤详情
     * @param stepId
     * @return
     */
    public static String genStepDetailKey(Long stepId) {
        return String.format("step_detail_%s", stepId);
    }

    public static final long EXPIRATION_TIME_STEP_DETAIL = -1;




}
