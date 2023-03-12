package com.testframe.autotest.cache.key;

public class SceneRecordCacheKeys {

    /**
     * Map 步骤执行记录缓存,永不过期
     * key: 执行记录id
     * value: 执行记录信息
     * @param sceneId
     * @return
     */
    public static String genSceneExeRecordKey(Long sceneId) {
        return String.format("scene_execute_record_%s", sceneId);
    }

    public static final long EXPIRATION_TIME_SCENE_RECORD = 1 * 60 * 1000;


    /**
     * String 场景最近执行记录
     * @param sceneId
     * @return
     */
    public static String genSceneRecentlyExe(Long sceneId) {
        return String.format("scene_recently_exe_%s", sceneId);
    }

    public static final long EXPIRATION_TIME_SCENE_RECENTLY_EXE = 1 * 60 * 1000;


}
