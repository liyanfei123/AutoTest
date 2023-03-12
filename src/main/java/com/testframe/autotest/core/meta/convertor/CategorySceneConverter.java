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
        categoryScene.setSceneId(categorySceneDo.getSceneId());
        categoryScene.setIsDelete(categorySceneDo.getIsDelete());
        return categoryScene;
    }

    public CategorySceneDo PoToDo(CategoryScene categoryScene) {
        CategorySceneDo categorySceneDo = new CategorySceneDo();
        categorySceneDo.setId(categoryScene.getId());
        categorySceneDo.setCategoryId(categoryScene.getCategoryId());
        categorySceneDo.setSceneId(categoryScene.getSceneId());
        categorySceneDo.setIsDelete(categoryScene.getIsDelete());
        categorySceneDo.setCreateTime(categoryScene.getCreateTime());
        return categorySceneDo;
    }

    public CategorySceneDo DtoToDo(CategorySceneDto categorySceneDto) {
        CategorySceneDo categorySceneDo = new CategorySceneDo();
        categorySceneDo.setCategoryId(categorySceneDto.getCategoryId());
        categorySceneDo.setSceneId(categorySceneDto.getSceneId());
        return categorySceneDo;
    }

    public CategorySceneDo DtoToDo(CategorySceneDo categorySceneDo, CategorySceneDto categorySceneDto) {
        categorySceneDo.setCategoryId(categorySceneDto.getCategoryId());
        categorySceneDo.setSceneId(categorySceneDto.getSceneId());
        return categorySceneDo;
    }
}
