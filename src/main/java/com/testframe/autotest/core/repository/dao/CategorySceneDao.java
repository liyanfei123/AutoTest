package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.CategoryDetail;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.repository.mapper.CategorySceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CategorySceneDao {

    @Autowired
    private CategorySceneMapper categorySceneMapper;

    public Long saveCategoryScene(CategoryScene categoryScene) {
        Long currentTime = System.currentTimeMillis();
        categoryScene.setCreateTime(currentTime);
        categoryScene.setUpdateTime(currentTime);
        if (categorySceneMapper.insertSelective(categoryScene) > 0 ) {
            return categoryScene.getId();
        }
        return 0L;
    }

    public Boolean updateCategoryScene(CategoryScene categoryScene) {
        Long currentTime = System.currentTimeMillis();
        categoryScene.setUpdateTime(currentTime);
        return categorySceneMapper.updateByPrimaryKeySelective(categoryScene) > 0;
    }


    public List<CategoryScene> queryByCategoryId(Integer categoryId) {
        return categorySceneMapper.selectByCategoryId(categoryId);
    }

    public CategoryScene queryBySceneId(Long sceneId) {
        return categorySceneMapper.selectBySceneId(sceneId);
    }
}
