package com.testframe.autotest.cache.service;

import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;

import java.util.HashMap;
import java.util.List;

public interface CategoryCacheService {

    /**
     * 获取目录信息
     * @param categoryId
     * @return
     */
    CategoryDetailCo getCategoryInfo(Integer categoryId);

    /**
     * 获取当前目录下的子目录
     * @param categoryId
     * @return
     */
    HashMap<Integer, CategoryDetailCo> getCategoryIn(Integer categoryId);

    /**
     * 获取一级目录，即最顶层目录
     * @return
     */
    HashMap<Integer, CategoryDetailCo> getFirstCategory();

    /**
     * 查询目录下的场景
     * @param categoryId
     * @param pageQry
     * @return
     */
    List<CategorySceneDto> sceneCategoryRelFromCache(Integer categoryId, PageQry pageQry);
}
