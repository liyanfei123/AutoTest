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

    public static final long EXPIRATION_TIME_SCENE_DETAIL = 5 * 60 * 1000;

    /**
     * 场景总数
     * @return
     */
    public static String genSceneCount() {
        return String.format("scene_total_count");
    }

    public static final long EXPIRATION_TIME_SCENE_COUNT = 1 * 60 * 1000;


}
