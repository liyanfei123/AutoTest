package com.testframe.autotest.core.repository;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.po.CategoryDetail;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.meta.request.PageQry;
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
    private CategoryCache categoryCache;

    @Autowired
    private CategorySceneConverter categorySceneConverter;


    @Transactional(rollbackFor = Exception.class)
    public Long saveCategoryScene(CategorySceneDo categoryDetailDo) {
        CategoryScene categoryScene = categorySceneConverter.DoToPo(categoryDetailDo);
        Long id = categorySceneDao.saveCategoryScene(categoryScene);
        categoryDetailDo.setId(id);
        categoryCache.updateSceneToCategory(categoryDetailDo.getCategoryId(), categoryDetailDo);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategoryScene(CategorySceneDo categoryDetailDo) {
        CategoryScene categoryScene = categorySceneConverter.DoToPo(categoryDetailDo);
        if (!categorySceneDao.updateCategoryScene(categoryScene)) {
            throw new AutoTestException("类目场景关联更新失败");
        }
        categoryCache.updateSceneToCategory(categoryDetailDo.getCategoryId(), categoryDetailDo);
        return true;
    }

    public Long countByCategoryId(Long categoryId) {
        Long count = categorySceneDao.countByCategoryId(categoryId);
        if (count == null) {
            return 0L;
        }
        return count;
    }

    public List<CategorySceneDo> queryByCategoryId(Integer categoryId, PageQry pageQry) {
        Long start = pageQry.getOffset();
        Long end = pageQry.getOffset() + pageQry.getSize();
        if (pageQry.getSize() == -1) {
            end = -1L;
        }
        List<CategorySceneDo> categorySceneDos = categoryCache.getSceneInCategory(categoryId, start, end);
        log.info("[CategorySceneRepository:queryByCategoryId] get scene from cache, in category {}, result = {}",
                categoryId, categorySceneDos);
        if (categorySceneDos == null || categorySceneDos.isEmpty()) {
            List<CategoryScene> categoryScenes = categorySceneDao.queryByCategoryId(categoryId, pageQry);
            if (categoryScenes.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            categorySceneDos = categoryScenes.stream().map(categorySceneConverter::PoToDo)
                    .collect(Collectors.toList());
            log.info("[CategorySceneRepository:queryByCategoryId] get scene from db, in category {}, result = {}",
                    categoryId, categorySceneDos);
            categoryCache.updateSceneInCategorys(categoryId, categorySceneDos);
        }
        return categorySceneDos;
    }

    public CategorySceneDo queryBySceneId(Long sceneId) {
        CategoryScene categoryScene = categorySceneDao.queryBySceneId(sceneId);
        if (categoryScene == null) {
            return null;
        }
        CategorySceneDo categorySceneDo = categorySceneConverter.PoToDo(categoryScene);
        categoryCache.updateSceneToCategory(categorySceneDo.getCategoryId(), categorySceneDo);
        return categorySceneDo;
    }
}
