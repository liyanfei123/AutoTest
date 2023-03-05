package com.testframe.autotest.domain.category;

import com.testframe.autotest.meta.bo.CategorySceneBo;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;

public interface CategorySceneDomain {

    Boolean sceneInCategory(Integer categoryId);

    Boolean updateCategoryScene(CategorySceneDto categorySceneDto);

    Boolean deleteCategoryScene(CategorySceneDto categorySceneDto);

    CategorySceneBo listSceneInCategory(Integer categoryId);
}
