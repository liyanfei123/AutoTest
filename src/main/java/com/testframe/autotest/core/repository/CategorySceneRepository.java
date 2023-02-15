package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.po.CategoryDetail;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.repository.dao.CategorySceneDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategorySceneRepository {

    @Autowired
    private CategorySceneDao categorySceneDao;

    @Autowired
    private CategorySceneConverter categorySceneConverter;


    @Transactional(rollbackFor = Exception.class)
    public Long saveCategoryScene(CategorySceneDo categoryDetailDo) {
        CategoryScene categoryScene = categorySceneConverter.DoToPo(categoryDetailDo);
        Long id = categorySceneDao.saveCategoryScene(categoryScene);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategoryScene(CategorySceneDo categoryDetailDo) {
        CategoryScene categoryScene = categorySceneConverter.DoToPo(categoryDetailDo);
        if (!categorySceneDao.updateCategoryScene(categoryScene)) {
            throw new AutoTestException("类目场景关联更新失败");
        }
        return true;
    }

    public List<CategorySceneDo> queryByCategoryId(Integer categoryId) {
        List<CategoryScene> categoryScenes = categorySceneDao.queryByCategoryId(categoryId);
        if (categoryScenes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<CategorySceneDo> categorySceneDos = categoryScenes.stream().map(categorySceneConverter::PoToDo)
                .collect(Collectors.toList());
        return categorySceneDos;
    }

    public CategorySceneDo queryBySceneId(Long sceneId) {
        CategoryScene categoryScene = categorySceneDao.queryBySceneId(sceneId);
        if (categoryScene == null) {
            return null;
        }
        CategorySceneDo categorySceneDo = categorySceneConverter.PoToDo(categoryScene);
        return categorySceneDo;
    }
}
