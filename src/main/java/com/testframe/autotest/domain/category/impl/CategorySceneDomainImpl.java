package com.testframe.autotest.domain.category.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.cache.service.CategoryCacheService;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.CategoryDetailRepository;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.meta.bo.CategorySceneBo;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CategorySceneDomainImpl implements CategorySceneDomain {


    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private CategorySceneConverter categorySceneConverter;

    @Autowired
    private CategoryCacheService categoryCacheService;

    // 判断当前类目下是否有生效的场景
    // false表示不存在，true表示存在
    @Override
    public Boolean sceneValidInCategory(Integer categoryId) {
        // step1. 判断是否存在子目录
        CategoryQry categoryQry = new CategoryQry(null, null, categoryId, null);
        HashMap<Integer, CategoryDetailCo> sonCategoryMap = categoryCacheService.getCategoryIn(categoryId);
        Collection<CategoryDetailCo> categoryDetailCos = sonCategoryMap.values(); // 子目录
        if (categoryDetailCos.isEmpty()) {
            // 判断是否存在关联的有效场景
            PageQry pageQry = new PageQry();
            pageQry.setSize(1);
            List<CategorySceneDto> validSceneDtos = categoryCacheService.sceneCategoryRelFromCache(categoryId, pageQry);
            return !validSceneDtos.isEmpty();
        }
        for (CategoryDetailCo categoryDetailCo : categoryDetailCos) {
            Boolean res = sceneValidInCategory(categoryDetailCo.getCategoryId());
            log.info("[CategorySceneDomainImpl:sceneInCategory] category have valid scene, categoryId = {}, res = {}",
                    categoryDetailCo.getCategoryId(), res);
            return res;
        }
        return false;
    }

    /**
     * 新增
     * @param categorySceneDto
     * @return
     */
    @Override
    public Boolean addCategoryScene(CategorySceneDto categorySceneDto) {
        log.info("[CategorySceneDomainImpl:updateCategoryScene] param = {}",
                JSON.toJSONString(categorySceneDto));
        try {
            CategorySceneDo categorySceneDo = categorySceneConverter.DtoToDo(categorySceneDto);
            categorySceneDo.setIsDelete(0);
            log.info("[CategorySceneDomainImpl:updateCategoryScene] add category-scene, category-scene = {}",
                    JSON.toJSONString(categorySceneDo));
            categorySceneRepository.saveCategoryScene(categorySceneDo);
        } catch (Exception e) {
            log.error("[CategorySceneDomainImpl:updateCategoryScene] update category-scene error, reason = {}", e);
            return false;
        }
        return true;
    }

    /**
     * 批量更新
     * @param categorySceneDtos
     * @return
     */
    @Override
    public Boolean batchUpdateCategoryScene(Integer oldCategoryId, List<CategorySceneDto> categorySceneDtos) {
        Map<Integer, List<CategorySceneDo>> updateCateSceneMap = new HashMap<>();
        List<CategorySceneDo> categorySceneDos = new ArrayList<>();
        for (CategorySceneDto categorySceneDto : categorySceneDtos) {
            CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(categorySceneDto.getSceneId()); // 原数据
            if (categorySceneDto.getCategoryId() == categorySceneDo.getCategoryId()
                    && categorySceneDto.getSceneId() == categorySceneDo.getSceneId()) {
                continue;
            }
            categorySceneDo = categorySceneConverter.DtoToDo(categorySceneDo, categorySceneDto);
            categorySceneDos.add(categorySceneDo);
        }
        updateCateSceneMap.put(oldCategoryId, categorySceneDos);
        log.debug("[CategorySceneDomainImpl:batchUpdateCategoryScene] batch update category-scene, category-scene = {}",
                JSON.toJSONString(updateCateSceneMap));
        return categorySceneRepository.batchUpdateCategoryScene(updateCateSceneMap);
    }

    @Override
    public Boolean deleteCategoryScene(CategorySceneDto categorySceneDto) {
        log.info("[CategorySceneDomainImpl:deleteCategoryScene] param = {}",
                JSON.toJSONString(categorySceneDto));
        try {
            CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(categorySceneDto.getSceneId());
            if (categorySceneDo == null) {
                throw new AutoTestException("当前删除的关联关系不存在");
            }
            categorySceneDo.setIsDelete(0);
            categorySceneRepository.batchUpdateCategoryScene(
                    this.buildUpdateCateSceneMap(categorySceneDo.getCategoryId(), categorySceneDo));
        } catch (Exception e) {
            log.error("[CategorySceneDomainImpl:deleteCategoryScene] delete category-scene error, reason = {}", e);
            return false;
        }
        return true;
    }

    @Override
    public CategorySceneBo listSceneInCategory(Integer categoryId) {
        PageQry pageQry = new PageQry();
        pageQry.setSize(-1);
        List<CategorySceneDto> categorySceneDtos = categoryCacheService.sceneCategoryRelFromCache(categoryId, pageQry);
        if (categorySceneDtos.isEmpty()) {
            return null;
        }
        List<Long> sceneIds = categorySceneDtos.stream().map(CategorySceneDto::getSceneId).collect(Collectors.toList());
        CategorySceneBo categorySceneBo = new CategorySceneBo();
        categorySceneBo.setCategoryId(categoryId);
        categorySceneBo.setSceneIds(sceneIds);
        return categorySceneBo;
    }

    private Map<Integer, List<CategorySceneDo>> buildUpdateCateSceneMap(Integer oldCategoryId, CategorySceneDo newCategorySceneDo) {
        List<CategorySceneDo> categorySceneDos = Arrays.asList(newCategorySceneDo);
        Map<Integer, List<CategorySceneDo>> updateCateSceneMap = new HashMap<>();
        updateCateSceneMap.put(oldCategoryId, categorySceneDos);
        return updateCateSceneMap;
    }
}
