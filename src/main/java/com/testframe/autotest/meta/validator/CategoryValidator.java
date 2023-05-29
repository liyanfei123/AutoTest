package com.testframe.autotest.meta.validator;

import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.cache.service.CategoryCacheService;
import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.CategoryTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.repository.CategoryDetailRepository;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.meta.command.SceneCategoryCmd;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryValidator {

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Autowired
    private CategoryDomain categoryDomain;

    public void checkCategoryUpdate(SceneCategoryCmd sceneCategoryCmd) {
        if (sceneCategoryCmd.getCategoryId() != null && sceneCategoryCmd.getCategoryId() > 0) {
            checkCategoryId(sceneCategoryCmd);
            checkRelatedCategoryId(sceneCategoryCmd);
            checkCategoryName(sceneCategoryCmd);
        } else {
            if (sceneCategoryCmd.getRelatedCategoryId() > 0) {
                checkRelatedCategoryId(sceneCategoryCmd);
            }
            checkCategoryName(sceneCategoryCmd);
        }
    }

    public void checkCategoryId(SceneCategoryCmd sceneCategoryCmd) {
        CategoryDetailCo categoryDetailCo = categoryCacheService.getCategoryInfo(sceneCategoryCmd.getCategoryId());
        if (categoryDetailCo == null) {
            throw new AutoTestException("当前类目id错误");
        }
    }

    public void checkCategoryId(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new AutoTestException("当前类目id错误");
        }
        CategoryDetailCo categoryDetailCo = categoryCacheService.getCategoryInfo(categoryId);
        if (categoryDetailCo == null) {
            throw new AutoTestException("当前类目id错误");
        }
    }

    public void checkCategoryName(SceneCategoryCmd sceneCategoryCmd) {
        if (sceneCategoryCmd.getCategoryName() != null
                && sceneCategoryCmd.getCategoryName().length() > autoTestConfig.getCategoryNameLength()) {
            throw new AutoTestException("类目名称超过上限");
        }
        if (sceneCategoryCmd.getCategoryName() != null) {
            List<CategoryDetailCo> categoryDetailCos = new ArrayList<>();
            if (sceneCategoryCmd.getRelatedCategoryId() != null && sceneCategoryCmd.getRelatedCategoryId() > 0) {
                // 新增多级类目
                categoryDetailCos = categoryDomain.querySonCategories(sceneCategoryCmd.getRelatedCategoryId());
            } else {
                // 新增顶级类目
                categoryDetailCos = categoryDomain.querySonCategories(null);
            }
            categoryDetailCos.stream().filter(categoryDetailCo -> !(categoryDetailCo.getCategoryId()
                    ==sceneCategoryCmd.getCategoryId())).collect(Collectors.toList()); // 过滤掉更新的当前类目
            if (categoryDetailCos.isEmpty()) {
                return;
            }
            List<String> categoryNames = categoryDetailCos.stream().map(CategoryDetailCo::getCategoryName)
                    .collect(Collectors.toList());
            if (categoryNames.contains(sceneCategoryCmd.getCategoryName())) {
                throw new AutoTestException("存在同名类目,请修改名称");
            }
        }
    }

    public void checkRelatedCategoryId(SceneCategoryCmd sceneCategoryCmd) {
        if (sceneCategoryCmd.getRelatedCategoryId() != null && sceneCategoryCmd.getRelatedCategoryId() > 0) {
            CategoryQry categoryQry = new CategoryQry(sceneCategoryCmd.getRelatedCategoryId(), null, null, null);
            List<CategoryDetailDo> relatedCategoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
            if (relatedCategoryDetailDos.isEmpty()) {
                throw new AutoTestException("当前关联类目id错误");
            }
        }
    }

}
