package com.testframe.autotest.domain.category.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.cache.service.CategoryCacheService;
import com.testframe.autotest.core.enums.CategoryTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.convertor.CategoryDetailConverter;
import com.testframe.autotest.core.repository.CategoryDetailRepository;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.meta.bo.CategoryDetailBo;
import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CategoryDomainImpl implements CategoryDomain {

    @Autowired
    private CategoryDetailConverter categoryDetailConverter;

    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private CategorySceneDomain categorySceneDomain;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Override
    public List<CategoryDetailBo> listCategory() {
        List<CategoryDetailBo> categoryDetailBos = new ArrayList<>();
        List<CategoryDetailCo> categoryDetailCos = this.querySonCategories(null);
        for (CategoryDetailCo categoryDetailCo: categoryDetailCos) {
            CategoryDetailBo categoryDetailBo = new CategoryDetailBo();
            categoryDetailBo.setCategoryId(categoryDetailCo.getCategoryId());
            categoryDetailBo.setCategoryName(categoryDetailCo.getCategoryName());
            categoryDetailBo.setCategories(categoryIn(categoryDetailCo.getCategoryId()));
            categoryDetailBos.add(categoryDetailBo);
        }
        return categoryDetailBos;
    }

    private List<CategoryDetailBo> categoryIn(Integer categoryId) {
        log.info("[CategoryDomainImpl:categoryIn] param = {}", JSON.toJSONString(categoryId));
        List<CategoryDetailBo> categoryDetailBos = new ArrayList<>();
        List<CategoryDetailCo> categoryDetailCos = this.querySonCategories(categoryId);
        if (categoryDetailCos.isEmpty()) {
            // 无子目录
            return Collections.EMPTY_LIST;
        }
        for (CategoryDetailCo categoryDetailCo : categoryDetailCos) {
            CategoryDetailBo categoryDetailBo = new CategoryDetailBo();
            categoryDetailBo.setCategoryId(categoryDetailCo.getCategoryId());
            categoryDetailBo.setCategoryName(categoryDetailCo.getCategoryName());
            categoryDetailBo.setCategories(categoryIn(categoryDetailCo.getCategoryId()));
            categoryDetailBos.add(categoryDetailBo);
        }
        return categoryDetailBos;
    }

    @Override
    public List<CategoryDetailCo> querySonCategories(Integer categoryId) {
        HashMap<Integer, CategoryDetailCo> categoryMap = new HashMap<>();
        if (categoryId != null && categoryId > 0) {
            // 多级类目下的子类目
            categoryMap = categoryCacheService.getCategoryIn(categoryId);
        } else {
            // 顶级类目
            categoryMap = categoryCacheService.getFirstCategory();
        }
        if (categoryMap == null) {
            return Collections.EMPTY_LIST;
        } else {
            Collection<CategoryDetailCo> categoryDetailCos = categoryMap.values();
            // 根据创建时间从小到大排序
            categoryDetailCos.stream().sorted(Comparator.comparing(CategoryDetailCo::getCreateTime)).collect(Collectors.toList());
//            categoryDetailCos.stream().sorted((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime())).collect(Collectors.toList());
            return categoryDetailCos.stream().collect(Collectors.toList());
        }

    }

    @Override
    public Integer updateCategory(CategoryDto categoryDto) {
        log.info("[CategoryDomainImpl:updateCategory] param = {}", JSON.toJSONString(categoryDto));
        try {
            if (categoryDto.getCategoryId() > 0) {
                // 更新
                CategoryQry categoryQry = new CategoryQry(categoryDto.getCategoryId(), null, null, null);
                List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
                CategoryDetailDo updateCategoryDetailDo = categoryDetailDos.get(0);
                updateCategoryDetailDo = categoryDetailConverter.DtoToDo(updateCategoryDetailDo, categoryDto);
                log.info("[CategoryDomainImpl:updateCategory] update category, category = {}",
                        JSON.toJSONString(updateCategoryDetailDo));
                categoryDetailRepository.updateCategory(updateCategoryDetailDo);
            } else {
                // 新增
                CategoryDetailDo newCategoryDetailDo = categoryDetailConverter.DtoToDo(categoryDto);
                newCategoryDetailDo.setType(categoryDto.getRelatedCategoryId() > 0 ? CategoryTypeEnum.MULTI.getType() :
                        CategoryTypeEnum.PRIMARY.getType());
                newCategoryDetailDo.setIsDelete(0);
                log.info("[CategoryDomainImpl:updateCategory] add category, category = {}",
                        JSON.toJSONString(newCategoryDetailDo));
                return categoryDetailRepository.saveCategory(newCategoryDetailDo);
            }
        } catch (Exception e) {
            log.error("[CategoryDomainImpl:updateCategory] error, reason = {}", e);
            throw new AutoTestException(e.getMessage());
        }
        return 0;
    }

    @Override
    public Boolean deleteCategory(Integer categoryId) {
        // 删除，同时判断当前类目下是否有关联的场景
        CategoryDetailDo categoryDetailDo = getCategory(categoryId);
        if (categoryDetailDo == null) {
            throw new AutoTestException("当前目录不存在");
        }
        List<CategoryDetailCo> categoryDetailCos = this.querySonCategories(categoryId);
        if (!categoryDetailCos.isEmpty()) {
            throw new AutoTestException("当前目录下有子目录，不允许删除");
        }
        Boolean sceneExist = categorySceneDomain.sceneValidInCategory(categoryId);
        if (sceneExist) {
            throw new AutoTestException("当前目录下有关联的场景，不允许删除");
        }
        categoryDetailDo.setIsDelete(1);
        log.info("[CategoryDomainImpl:deleteCategory] delete category, category = {}",
                JSON.toJSONString(categoryDetailDo));
        categoryDetailRepository.deleteCategory(categoryId);
        return true;
    }

    @Override
    public CategoryDetailBo getCategoryById(Integer categoryId) {
        CategoryDetailDo categoryDetailDo = getCategory(categoryId);
        if (categoryDetailDo == null) {
            return null;
        }
        CategoryDetailBo categoryDetailBo = new CategoryDetailBo();
        categoryDetailBo.setCategoryId(categoryDetailDo.getCategoryId());
        categoryDetailBo.setCategoryName(categoryDetailDo.getCategoryName());
        categoryDetailBo.setCategories(null);
        log.info("[CategoryDomainImpl:getCategoryById] get category, categoryId = {}, category = {}",
                categoryId, JSON.toJSONString(categoryDetailDo));
        return categoryDetailBo;
    }

    private CategoryDetailDo getCategory(Integer categoryId) {
        CategoryQry categoryQry = new CategoryQry(categoryId, null, null, null);
        List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
        if (categoryDetailDos.size() == 0) {
            return null;
        }
        CategoryDetailDo categoryDetailDo = categoryDetailDos.get(0);
        return categoryDetailDo;
    }


}
