package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import org.springframework.stereotype.Component;

@Component
public class CategorySceneConverter {

    public CategoryScene DoToPo(CategorySceneDo categorySceneDo) {
        CategoryScene categoryScene = new CategoryScene();
        categoryScene.setId(categorySceneDo.getId());
        categoryScene.setCategoryId(categorySceneDo.getCategoryId());
        categoryScene.setStepId(categorySceneDo.getStepId());
        categoryScene.setSceneId(categorySceneDo.getSceneId());
        categoryScene.setSetId(categorySceneDo.getSetId());
        categoryScene.setIsDelete(categorySceneDo.getIsDelete());
        return categoryScene;
    }

    public CategorySceneDto DoToDto(CategorySceneDo categorySceneDo) {
        CategorySceneDto categorySceneDto = new CategorySceneDto();
        categorySceneDto.setCategoryId(categorySceneDo.getCategoryId());
        categorySceneDto.setSceneId(categorySceneDo.getSceneId());
        return categorySceneDto;
    }

    public CategorySceneDo PoToDo(CategoryScene categoryScene) {
        CategorySceneDo categorySceneDo = new CategorySceneDo();
        categorySceneDo.setId(categoryScene.getId());
        categorySceneDo.setCategoryId(categoryScene.getCategoryId());
        categorySceneDo.setStepId(categoryScene.getStepId());
        categorySceneDo.setSceneId(categoryScene.getSceneId());
        categorySceneDo.setSetId(categoryScene.getSetId());
        categorySceneDo.setIsDelete(categoryScene.getIsDelete());
        categorySceneDo.setCreateTime(categoryScene.getCreateTime());
        return categorySceneDo;
    }

    public CategorySceneDo DtoToDo(CategorySceneDto categorySceneDto) {
        CategorySceneDo categorySceneDo = new CategorySceneDo();
        categorySceneDo.setCategoryId(categorySceneDto.getCategoryId());
        categorySceneDo.setStepId(categorySceneDto.getStepId());
        categorySceneDo.setSceneId(categorySceneDto.getSceneId());
        categorySceneDo.setSetId(categorySceneDto.getSetId());
        return categorySceneDo;
    }

    public CategorySceneDo DtoToDo(CategorySceneDo categorySceneDo, CategorySceneDto categorySceneDto) {
        categorySceneDo.setCategoryId(categorySceneDto.getCategoryId());
        categorySceneDo.setStepId(categorySceneDto.getStepId());
        categorySceneDo.setSceneId(categorySceneDto.getSceneId());
        categorySceneDo.setSetId(categorySceneDto.getSetId());
        return categorySceneDo;
    }
}
