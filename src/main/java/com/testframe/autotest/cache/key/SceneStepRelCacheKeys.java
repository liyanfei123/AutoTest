package com.testframe.autotest.cache.key;

public class SceneStepRelCacheKeys {


    /**
     * hash 当前场景下的所有步骤
     * key:stepId
     * value:StepDetailDto
     * @param sceneId
     * @return
     */
    public static String genStepInScene(Long sceneId) {
        return String.format("steps_in_scene_%s", sceneId);
    }

    public static final long EXPIRATION_TIME_STEP_SCENE = 1 * 60 * 1000;


}
