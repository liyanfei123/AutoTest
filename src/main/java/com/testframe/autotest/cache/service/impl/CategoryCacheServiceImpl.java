package com.testframe.autotest.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.cache.service.CategoryCacheService;
import com.testframe.autotest.core.enums.CategoryTypeEnum;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.convertor.CategoryDetailConverter;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.CategoryDetailRepository;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CategoryCacheServiceImpl implements CategoryCacheService {

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private CategoryDetailConverter categoryDetailConverter;

    @Autowired
    private CategorySceneConverter categorySceneConverter;

    @Override
    public CategoryDetailCo getCategoryInfo(Integer categoryId) {
        if (categoryId <= 0) {
            return null;
        }
        CategoryDetailCo categoryDetailCo = categoryCache.getCategoryInfo(categoryId);
        if (categoryDetailCo != null) {
            return categoryDetailCo;
        } else {
            CategoryQry categoryQry = new CategoryQry(categoryId, null, null, null);
            List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
            if (categoryDetailDos.isEmpty()) {
                return null;
            }
            CategoryDetailDo categoryDetailDo = categoryDetailDos.get(0);
            categoryDetailCo = categoryDetailConverter.DoToCo(categoryDetailDo);
            try {
                categoryCache.updateCategoryInfo(categoryId, categoryDetailCo);
            } catch (Exception e) {
                // 更新缓存异常，将key删除
                log.error("[CategoryDomainImpl:getCategoryInfo] refresh category info fail, reason = {}", e);
                categoryCache.clearCategoryInfo(categoryId);
            }
            return categoryDetailCo;
        }

    }

    @Override
    public HashMap<Integer, CategoryDetailCo> getCategoryIn(Integer categoryId) {
        // 查询所有一级类目 先查缓存，若缓存不存在，则读取db进行刷新
        HashMap<Integer, CategoryDetailCo> categoryDetailCoMap = categoryCache.getLevelCategory(categoryId);
        if (categoryDetailCoMap != null && !categoryDetailCoMap.values().isEmpty()) {
            return categoryDetailCoMap;
        } else {
            // 回刷缓存
            // 查询当前类目下的子类目
            CategoryQry categoryQry = new CategoryQry(null, null, categoryId, null);
            List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
            if (categoryDetailDos.isEmpty()) {
                return null;
            }
            List<CategoryDetailCo> categoryDetailCosFromDB = categoryDetailDos.stream().map(categoryDetailConverter::DoToCo)
                    .collect(Collectors.toList());
            HashMap<Integer, CategoryDetailCo> categoryDetailCoMapFromDB = new HashMap<>();
            categoryDetailCosFromDB.forEach(categoryDetailCo ->
                    categoryDetailCoMapFromDB.put(categoryDetailCo.getCategoryId(), categoryDetailCo));
            try {
                categoryCache.batchUpdateLevelCategory(categoryId, categoryDetailCosFromDB);
            } catch (Exception e) {
                // 更新缓存异常，将key删除
                log.error("[CategoryDomainImpl:querySonCategories] refresh level category fail, reason = {}", e);
                categoryCache.clearLevelCategory(categoryId, null);
            }
            return categoryDetailCoMapFromDB;
        }
    }

    @Override
    public HashMap<Integer, CategoryDetailCo> getFirstCategory() {
        // 查询所有顶级类目
        HashMap<Integer, CategoryDetailCo> categoryDetailCoMap = categoryCache.getFirstCategory();
        if (categoryDetailCoMap != null && !categoryDetailCoMap.values().isEmpty()) {
            return categoryDetailCoMap;
        } else {
            CategoryQry categoryQry = new CategoryQry(null, null, null, CategoryTypeEnum.PRIMARY.getType());
            List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
            List<CategoryDetailCo> categoryDetailCosFromDB = categoryDetailDos.stream().map(categoryDetailConverter::DoToCo).collect(Collectors.toList());
            HashMap<Integer, CategoryDetailCo> categoryDetailCoMapFromDB = new HashMap<>();
            categoryDetailCosFromDB.forEach(categoryDetailCo ->
                    categoryDetailCoMapFromDB.put(categoryDetailCo.getCategoryId(), categoryDetailCo));
            if (categoryDetailDos.isEmpty()) {
                return null;
            }
            try {
                categoryCache.batchUpdateFirstCategory(categoryDetailCosFromDB);
            } catch (Exception e) {
                // 更新缓存异常，将key删除
                log.error("[CategoryDomainImpl:querySonCategories] refresh top category fail, reason = {}", e);
                categoryCache.clearFirstCategory(null);
            }
            return categoryDetailCoMapFromDB;
        }
    }

    public List<CategorySceneDto> sceneCategoryRelFromCache(Integer categoryId, PageQry pageQry) {
        Long start = pageQry.getOffset();
        Long end = pageQry.getOffset() + pageQry.getSize();
        if (pageQry.getSize() == -1) {
            end = -1L;
        }
        List<CategorySceneDto> categorySceneDtos;
        List<CategorySceneDo> categorySceneDos = categoryCache.getSceneInCategory(categoryId, start, end);
        if (categorySceneDos == null || categorySceneDos.isEmpty()) {
            categorySceneDos = categorySceneRepository.querySceneByCategoryId(categoryId, pageQry);
            if (categorySceneDos.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            log.info("[CategoryCacheServiceImpl:sceneCategoryRelFromCache] get scene-category relation from db, result = {}",
                    JSON.toJSONString(categorySceneDos));
            // 更新缓存
            categoryCache.updateSceneInCategorys(categoryId, categorySceneDos);
        }
        categorySceneDtos = categorySceneDos.stream().map(categorySceneConverter::DoToDto)
                .collect(Collectors.toList());
        return categorySceneDtos;
    }
}
