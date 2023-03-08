package com.testframe.autotest.cache.key;

public class SceneDetailCacheKeys {



    /**
     * 场景信息缓存
     * @param sceneId
     * @return
     */
    public static String genSceneDetailKey(Long sceneId) {
        return String.format("scene_detail_%s", sceneId);
    }

    public static final long EXPIRATION_TIME_SCENE_DETAIL = 5 * 60 * 60 * 1000;

}
