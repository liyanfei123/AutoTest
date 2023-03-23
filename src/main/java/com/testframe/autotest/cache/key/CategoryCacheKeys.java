package com.testframe.autotest.cache.key;

public class CategoryCacheKeys {

    /**
     * Hash 所有一级类目
     * key: 子类目id
     * value: 子类目信息
     * @return
     */
    public static String genFirstCategoryKey() {
        return "category_in_first";
    }

    public static final long EXPIRATION_TIME_FIRST_CATEGORY = -1;

    /**
     * Hash 子父类目下的所有类目
     * key: 从属子类目id
     * value: 从属子类目信息
     * @param categoryId 父类目id
     * @return
     */
    public static String genLevelCategoryKey(Integer categoryId) {
        return String.format("category_in_%s", categoryId);
    }

    public static final long EXPIRATION_TIME_LEVEL_CATEGORY = -1;

    /**
     * zset 根据创建时间，从小到大排序
     * 当前类目id下的场景，根据创建时间进行排序
     * 删除的时候需要进行移除操作
     * @param categoryId
     * @return
     */
    public static String genSceneInCategoryKey(Integer categoryId) {
        return String.format("scene_in_category_%s", categoryId);
    }

    public static final long EXPIRATION_TIME_SCENE_CATEGORY = 1 * 60 * 1000;

    /**
     * set 所有类目id
     * @return
     */
    public static String genAllCategoryIdKey() {
        return "all_category_id";
    }

    public static final long EXPIRATION_TIME_ALL_CATEGORY_ID = -1; // 用不失效

}
