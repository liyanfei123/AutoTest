package com.testframe.autotest.domain.category;

import com.testframe.autotest.meta.bo.CategorySceneBo;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;

import java.util.List;

public interface CategorySceneDomain {

    Boolean sceneValidInCategory(Integer categoryId);

    Boolean addCategoryScene(CategorySceneDto categorySceneDto);

    Boolean batchUpdateCategoryScene(Integer oldCategoryId, List<CategorySceneDto> categorySceneDtos);

    Boolean deleteCategoryScene(CategorySceneDto categorySceneDto);

    CategorySceneBo listSceneInCategory(Integer categoryId);
}
