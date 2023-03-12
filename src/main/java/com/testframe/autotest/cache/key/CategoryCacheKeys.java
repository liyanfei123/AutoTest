package com.testframe.autotest.cache.key;

public class CategoryCacheKeys {


    /**
     * 类目信息
     * @param categoryId
     * @return
     */
    public static String genCategoryInfoKey(Integer categoryId) {
        return String.format("category_info_%s", categoryId);
    }

    public static final long EXPIRATION_TIME_CATEGORY_INFO = 1 * 60 * 1000;

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

}
