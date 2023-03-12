package com.testframe.autotest.core.repository;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.cache.ao.SceneStepRelCache;
import com.testframe.autotest.cache.ao.StepOrderCache;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.Do.SceneDo;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.dao.CategorySceneDao;
import com.testframe.autotest.core.repository.dao.SceneDetailDao;
import com.testframe.autotest.core.repository.dao.SceneStepDao;
import com.testframe.autotest.core.repository.dao.StepOrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.testframe.autotest.core.meta.po.SceneDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class SceneDetailRepository {

    @Autowired
    private SceneDetailDao sceneDao;

    @Autowired
    private SceneStepDao sceneStepDao;

    @Autowired
    private StepOrderDao stepOrderDao;

    @Autowired
    private CategorySceneDao categorySceneDao;

    @Autowired
    private SceneDetailCache sceneDetailCache;

    @Autowired
    private StepOrderCache stepOrderCache;

    @Autowired
    private SceneStepRelCache sceneStepRelCache;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;

    @Autowired
    private CategorySceneConverter categorySceneConverter;

    public List<SceneDetailDo> querySceneByTitle(String title) {
        List<SceneDetail> sceneDetails = sceneDao.querySceneByTitle(title);
        List<SceneDetailDo> sceneDetailDos = sceneDetails.stream().map(sceneDetailConvertor::PoToDo).collect(Collectors.toList());
        return sceneDetailDos;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveSceneInit(SceneDo sceneDo) {
        // 保存场景
        SceneDetail sceneDetail = sceneDetailConvertor.DoToPO(sceneDo.getSceneDetailDo());
        Long sceneId = sceneDao.saveScene(sceneDetail);
        if (sceneId == 0L) {
            throw new AutoTestException("新增场景失败");
        }
        // 保存场景类目关系
        sceneDo.getCategorySceneDo().setSceneId(sceneId);
        CategoryScene categoryScene = categorySceneConverter.DoToPo(sceneDo.getCategorySceneDo());
        if (categorySceneDao.saveCategoryScene(categoryScene) == 0L) {
            throw new AutoTestException("新增场景关联类目保存失败");
        }
        // 初始化步骤执行顺序
        List<Long> orderList = new ArrayList<>();
        StepOrder stepOrder = new StepOrder(null, sceneId, 0L, orderList.toString(),
                StepOrderEnum.BEFORE.getType(), null, null);
        if (!stepOrderDao.saveStepOrder(stepOrder)) {
            throw new AutoTestException("步骤执行顺序添加失败");
        }
        countScene(sceneId, null);
        return sceneId;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateScene(SceneDo sceneDo) {
        // 更新场景
        Long sceneId = sceneDo.getSceneDetailDo().getSceneId();
        SceneDetail sceneDetail = sceneDetailConvertor.DoToPO(sceneDo.getSceneDetailDo());
        log.info("[SceneDetailRepository:update] update scene, {}", JSON.toJSONString(sceneDetail));
        if (!sceneDao.updateScene(sceneDetail)) {
            throw new AutoTestException("场景更新失败");
        }
        // 删除缓存
        sceneDetailCache.clearSceneDetailCache(sceneId);
        // 更新类目
        if (sceneDo.getCategorySceneDo() != null) {
            CategorySceneDo categorySceneDo = sceneDo.getCategorySceneDo();
            Integer categoryId = categorySceneDo.getCategoryId();
            CategoryScene categoryScene = categorySceneConverter.DoToPo(categorySceneDo);
            if (!categorySceneDao.updateCategoryScene(categoryScene)) {
                throw new AutoTestException("新增场景关联类目保存失败");
            }
            categoryCache.delSceneInCategory(categoryId, sceneId);
        }
        return true;
    }

    public SceneDetailDo querySceneById(Long sceneId) {
        SceneDetail sceneDetail = sceneDao.querySceneById(sceneId);
        if (sceneDetail == null || sceneDetail.getIsDelete() == 1) {
            return null;
        } else {
            return sceneDetailConvertor.PoToDo(sceneDetail);
        }
    }

    public List<SceneDetailDo> batchQuerySceneByIds(List<Long> sceneIds) {
        List<SceneDetailDo> sceneDetailDos = new ArrayList<>();
        for (Long sceneId : sceneIds) {
            SceneDetailDo sceneDetailDo = querySceneById(sceneId);
            sceneDetailDos.add(sceneDetailDo);
        }
        return sceneDetailDos;
    }

    // delete
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteScene(Long sceneId) {
        SceneDetail sceneDetail = sceneDao.querySceneById(sceneId);
        sceneDetail.setIsDelete(1);
        if (!sceneDao.updateScene(sceneDetail)) {
            return false;
        }
        // 判断当前场景下是否存在有效步骤，若存在需要删除
        List<SceneStep> sceneSteps = sceneStepDao.queryBySceneId(sceneId);
        if (!sceneSteps.isEmpty()) {
            sceneSteps.forEach(sceneStep -> sceneStep.setIsDelete(1));
            for (SceneStep sceneStep : sceneSteps) {
                if (!sceneStepDao.updateSceneStep(sceneStep)) {
                    throw new AutoTestException("删除失败");
                }
            }
        }
        // 执行顺序删除
        List<StepOrder> stepOrders = stepOrderDao.getStepOrderBySceneIdAndType(sceneId, StepOrderEnum.BEFORE.getType());
        for (StepOrder stepOrder : stepOrders) {
            stepOrder.setOrderList(Collections.EMPTY_LIST.toString());
            stepOrderDao.updateStepOrder(stepOrder);
        }

        // 目录关联关系删除
        CategoryScene categoryScene = categorySceneDao.queryBySceneId(sceneId);
        categoryScene.setIsDelete(1);
        categorySceneDao.updateCategoryScene(categoryScene);

        sceneDetailCache.clearSceneDetailCache(sceneId);
        sceneDetailCache.decrCount(1);
        sceneStepRelCache.clearSceneStepRels(sceneId);
        stepOrderCache.clearBeforeStepOrderCache(sceneId);
        categoryCache.delSceneInCategory(categoryScene.getCategoryId(), sceneId);
        return true;
    }

    /**
     * 根据相关条件查找场景
     * 优先级：场景id, 场景名称
     * @param
     * @return
     */
    // TODO: 2023/2/27 添加场景状态查询，需要先查询最近执行状态的场景
    public List<SceneDetailDo> queryScenes(Long sceneId, String sceneName,
                                           Integer categoryId, Integer status,
                                           PageQry pageQry) {
        List<SceneDetailDo> scenes = new ArrayList<>();
        List<SceneDetail> sceneDetailList;
        if (sceneId != null && sceneId != 0L) {
            // 根据场景id搜索
            SceneDetailDo sceneDetailDo = querySceneById(sceneId);
            scenes.add(sceneDetailDo);
        } else if (sceneName != null && !sceneName.trim().equals("")) {
            // 根据场景名称搜索
            if (categoryId != null && categoryId > 0) {
                sceneDetailList = sceneDao.querySceneLikeTitleInCategory(sceneName, categoryId, pageQry);
            } else {
                sceneDetailList = sceneDao.querySceneLikeTitle(sceneName, pageQry);
            }
            scenes = sceneDetailList.stream().map(sceneDetailConvertor::PoToDo).collect(Collectors.toList());
        } else {
            // 全局无区别搜索列表
            sceneDetailList = sceneDao.queryScenes(pageQry);
            scenes = sceneDetailList.stream().map(sceneDetailConvertor::PoToDo).collect(Collectors.toList());
        }
        return scenes;
    }

    // 添加缓存计数
    private void countScene(Long sceneId, String sceneName) {
        Long count = sceneDetailCache.getSceneCount();
        if (count == null) {
            count = sceneDao.countScenes(sceneId, sceneName);
            sceneDetailCache.incrCount(count);
        } else {
            sceneDetailCache.incrCount(1);
        }
    }


}
