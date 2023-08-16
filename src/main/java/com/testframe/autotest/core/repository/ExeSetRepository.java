package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.convertor.ExeSetConverter;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.meta.po.ExeSet;
import com.testframe.autotest.core.repository.dao.CategorySceneDao;
import com.testframe.autotest.core.repository.dao.ExeSetDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ExeSetRepository {

    @Autowired
    private ExeSetDao exeSetDao;

    @Autowired
    private CategorySceneDao categorySceneDao;

    @Autowired
    private ExeSetConverter exeSetConverter;

    @Transactional(rollbackFor = Exception.class)
    public Long updateExeSet(ExeSetDo exeSetDo, Integer categoryId) {
        // 更新执行集信息
        ExeSet exeSet = exeSetConverter.DoToPo(exeSetDo);
        Long setId = exeSetDao.updateExeSet(exeSet);
        if (setId == null || setId == 0L) {
            throw new AutoTestException("执行集合更新失败");
        }

        // 更新绑定类目
        if (categoryId != null && categoryId > 0) {
            CategoryScene categoryScene = categorySceneDao.queryBySetId(exeSetDo.getSetId());
            if (categoryScene == null) {
                categoryScene = new CategoryScene();
                categoryScene.setCategoryId(categoryId);
                categoryScene.setSetId(setId);
                if (categorySceneDao.saveCategoryScene(categoryScene) <= 0L) {
                    throw new AutoTestException("执行集关联类目保存失败");
                }
            } else {
                categoryScene.setCategoryId(categoryId);
                if (!categorySceneDao.updateCategoryScene(categoryScene)) {
                    throw new AutoTestException("执行集关联类目更新失败");
                }
            }
        }
        return setId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteExeSet(Long setId) {
        ExeSet exeSet = exeSetDao.queryExeSetById(setId);
        if (exeSet == null) {
            return null;
        }
        exeSet.setIsDelete(1);
        return exeSetDao.updateExeSet(exeSet) > 0L ? true : false;
    }

    public ExeSetDo queryExeSetById(Long setId) {
        ExeSet exeSet = exeSetDao.queryExeSetById(setId);
        if (exeSet == null) {
            return null;
        }
        return exeSetConverter.PoToDo(exeSet);
    }

    public List<ExeSetDo> queryExeSetsByName(String setName) {
        List<ExeSet> exeSets = exeSetDao.querySetByName(setName);
        if (exeSets.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<ExeSetDo> exeSetDos = new ArrayList<>();
        exeSets.forEach(exeSet -> {
            ExeSetDo exeSetDo = exeSetConverter.PoToDo(exeSet);
            exeSetDos.add(exeSetDo);
        });
        return exeSetDos;
    }
}
