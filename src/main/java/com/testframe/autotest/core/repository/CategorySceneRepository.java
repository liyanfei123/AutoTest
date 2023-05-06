package com.testframe.autotest.core.repository;

import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.dao.CategorySceneDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
        List<CategorySceneDo> categorySceneDos = Arrays.asList(categoryDetailDo);
        categoryCache.updateSceneInCategorys(categoryDetailDo.getCategoryId(), categorySceneDos);
        return id;
    }


    /**
     * key 原目录id
     * value 更新后的绑定关系
     * @param categoryDetailDoMap
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateCategoryScene(Map<Integer, List<CategorySceneDo>> categoryDetailDoMap) {
        for (Integer categoryId : categoryDetailDoMap.keySet()) {
            List<CategorySceneDo> categorySceneDos = categoryDetailDoMap.get(categoryId);
            List<CategoryScene> categoryScenes = categorySceneDos.stream().map(categorySceneConverter::DoToPo)
                    .collect(Collectors.toList());
            List<Long> sceneIds = new ArrayList<>();
            Integer newCategoryId = null;
            for (CategoryScene categoryScene : categoryScenes) {
                if (!categorySceneDao.updateCategoryScene(categoryScene)) {
                    throw new AutoTestException("类目场景关联更新失败");
                }
                newCategoryId = categoryScene.getCategoryId();
                sceneIds.add(categoryScene.getSceneId());
            }
            // 删除原类目下的场景缓存
            categoryCache.clearSceneInCategory(categoryId, sceneIds);
            // 更新新类目下的场景缓存
            categoryCache.updateSceneInCategorys(newCategoryId, categorySceneDos);
        }
        return true;
    }

    public Long countByCategoryId(Long categoryId) {
        Long count = categorySceneDao.countByCategoryId(categoryId);
        if (count == null) {
            return 0L;
        }
        return count;
    }

    public List<CategorySceneDo> querySceneByCategoryId(Integer categoryId, PageQry pageQry) {
        List<CategoryScene> categoryScenes = categorySceneDao.queryByCategoryId(categoryId, pageQry);
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
        List<CategorySceneDo> categorySceneDos = Arrays.asList(categorySceneDo);
        categoryCache.updateSceneInCategorys(categorySceneDo.getCategoryId(), categorySceneDos);
        return categorySceneDo;
    }
}
