package com.testframe.autotest.core.repository;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.dao.SceneDetailDao;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.query.SceneQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.testframe.autotest.core.meta.po.SceneDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class SceneDetailRepository {

    @Autowired
    private SceneDetailDao sceneDao;

    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;


    public Boolean querySceneByTitle(String title) {
        return sceneDao.querySceneByTitle(title);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveScene(Scene sceneCreate) {
        SceneDetail sceneDetail = sceneDetailConvertor.toPO(sceneCreate);
        Long sceneId = sceneDao.saveScene(sceneDetail);
        if (sceneId <= 0 || sceneId == null) {
            return 0L;
        }
        return sceneId;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(Scene sceneUpdate) {
        SceneDetail sceneDetail = sceneDetailConvertor.toPO(sceneUpdate);
        log.info("[SceneDetailRepository:update] update scene, {}", JSON.toJSONString(sceneDetail));
        if (!sceneDao.updateScene(sceneDetail)) {
            throw new AutoTestException("场景更新失败");
        }
        return true;
    }

    public Scene querySceneById(Long sceneId) {
        SceneDetail sceneDetail = sceneDao.querySceneById(sceneId);
        if (sceneDetail == null || sceneDetail.getIsDelete() == 1) {
            return null;
        } else {
            return buildScene(sceneDetail);
        }

    }

    /**
     * 根据相关条件查找场景
     * 优先级：场景id, 场景名称
     * 多返回一个用户计算lastId
     * @param
     * @return
     */
    public List<Scene> queryScenes(Long sceneId, String sceneName, PageQry pageQry) {
        List<Scene> scenes = new ArrayList<>();
        if (sceneId != null && sceneId != 0L) {
            Scene scene = querySceneById(sceneId);
            scenes.add(scene);
        } else if (sceneName != null && !sceneName.trim().equals("")) {
            List<SceneDetail> sceneDetailList = sceneDao.querySceneLikeTitle(sceneName, pageQry);
            scenes = sceneDetailList.stream().map(this::buildScene).collect(Collectors.toList());
        } else {
            List<SceneDetail> sceneDetailList = sceneDao.queryScenes(pageQry);
            scenes = sceneDetailList.stream().map(this::buildScene).collect(Collectors.toList());
        }
        return scenes;
    }

    public Long countScene(Long sceneId, String sceneName) {
        return sceneDao.countScenes(sceneId, sceneName);
    }

    public Scene buildScene(SceneDetail sceneDetail) {
        if (sceneDetail == null) {
            return null;
        }
        Scene scene = new Scene();
        scene.setId(sceneDetail.getId());
        scene.setType(sceneDetail.getType());
        scene.setTitle(sceneDetail.getSceneName());
        scene.setDesc(sceneDetail.getSceneDesc());
        scene.setUrl(sceneDetail.getUrl());
        scene.setWaitType(sceneDetail.getWaitType());
        scene.setWaitTime(sceneDetail.getWaitTime());
        scene.setCreateBy(sceneDetail.getCreateBy());
        scene.setIsDelete(sceneDetail.getIsDelete());
        return scene;

    }
}
