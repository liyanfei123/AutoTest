package com.testframe.autotest.domain.category.impl;

import com.alibaba.fastjson.JSON;
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

import java.util.List;
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

    // 判断当前类目下是否有生效的场景
    // false表示不存在，true表示存在
    @Override
    public Boolean sceneInCategory(Integer categoryId) {
        // step1. 判断是否存在子目录
        CategoryQry categoryQry = new CategoryQry(null, null, categoryId, null);
        List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry); // 子目录
        if (categoryDetailDos.isEmpty()) {
            // 判断是否存在关联的有效场景
            PageQry pageQry = new PageQry();
            pageQry.setSize(1);
            List<CategorySceneDo> categorySceneDos = categorySceneRepository.queryByCategoryId(categoryId, pageQry);
            return !categorySceneDos.isEmpty();
        }
        for (CategoryDetailDo categoryDetailDo : categoryDetailDos) {
            Boolean res = sceneInCategory(categoryDetailDo.getCategoryId());
            log.info("[CategorySceneDomainImpl:sceneInCategory] category have valid scene, categoryId = {}, res = {}",
                    categoryDetailDo.getCategoryId(), res);
            return res;
        }
        return false;
    }

    @Override
    public Boolean updateCategoryScene(CategorySceneDto categorySceneDto) {
        log.info("[CategorySceneDomainImpl:updateCategoryScene] param = {}",
                JSON.toJSONString(categorySceneDto));
        try {
            CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(categorySceneDto.getSceneId());
            if (categorySceneDo == null) {
                // 新增
                categorySceneDo = categorySceneConverter.DtoToDo(categorySceneDto);
                categorySceneDo.setIsDelete(0);
                log.info("[CategorySceneDomainImpl:updateCategoryScene] add category-scene, category-scene = {}",
                        JSON.toJSONString(categorySceneDo));
                categorySceneRepository.saveCategoryScene(categorySceneDo);
            } else {
                // 更新
                if (categorySceneDto.getCategoryId() == categorySceneDo.getCategoryId()
                        && categorySceneDto.getSceneId() == categorySceneDo.getSceneId()) {
                    return true;
                }
                categorySceneDo = categorySceneConverter.DtoToDo(categorySceneDo, categorySceneDto);
                log.info("[CategorySceneDomainImpl:updateCategoryScene] update category-scene, category-scene = {}",
                        JSON.toJSONString(categorySceneDo));
                categorySceneRepository.updateCategoryScene(categorySceneDo);
            }
        } catch (Exception e) {
            log.error("[CategorySceneDomainImpl:updateCategoryScene] update category-scene error, reason = {}", e);
            return false;
        }
        return true;
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
            categorySceneRepository.updateCategoryScene(categorySceneDo);
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
        List<CategorySceneDo> categorySceneDos = categorySceneRepository.queryByCategoryId(categoryId, pageQry);
        if (categorySceneDos.isEmpty()) {
            return null;
        }
        List<Long> sceneIds = categorySceneDos.stream().map(CategorySceneDo::getSceneId).collect(Collectors.toList());
        CategorySceneBo categorySceneBo = new CategorySceneBo();
        categorySceneBo.setCategoryId(categoryId);
        categorySceneBo.setSceneIds(sceneIds);
        return categorySceneBo;
    }
}
