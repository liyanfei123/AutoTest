package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.meta.command.SceneCategoryCmd;
import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.service.SceneCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SceneCategoryServiceImpl implements SceneCategoryService {

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Autowired
    private CategoryDomain categoryDomain;

    @Override
    public Integer updateSceneCategory(SceneCategoryCmd sceneCategoryCmd) {
        if (sceneCategoryCmd.getCategoryName() != null
                && sceneCategoryCmd.getCategoryName().length() > autoTestConfig.getCategoryNameLength()) {
            throw new AutoTestException("类目名称超过上限");
        }
        if (sceneCategoryCmd.getCategoryId() > 0 && sceneCategoryCmd.getRelatedCategoryId() > 0
                && sceneCategoryCmd.getCategoryId() == sceneCategoryCmd.getRelatedCategoryId()) {
            throw new AutoTestException("类目id不可与自己绑定");
        }
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(sceneCategoryCmd.getCategoryId());
        categoryDto.setCategoryName(sceneCategoryCmd.getCategoryName());
        categoryDto.setRelatedCategoryId(sceneCategoryCmd.getRelatedCategoryId());
        return categoryDomain.updateCategory(categoryDto);
    }

    @Override
    public Boolean deleteSceneCategory(Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            throw new AutoTestException("请输入正确的类目id");
        }
        return categoryDomain.deleteCategory(categoryId);
    }
}
