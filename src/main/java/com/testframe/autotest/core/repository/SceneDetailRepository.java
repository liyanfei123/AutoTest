package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.repository.dao.SceneDetailDao;
import com.testframe.autotest.meta.bo.Scene;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.testframe.autotest.core.meta.po.SceneDetail;


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
        sceneDao.saveScene(sceneDetail);
        if (sceneDetail.getId() <= 0 || sceneDetail.getId() == null) {
            return null;
        }
        return sceneDetail.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(Scene sceneUpdate) {
        SceneDetail sceneDetail = sceneDetailConvertor.toPO(sceneUpdate);
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
            Scene scene = new Scene();
            scene.setId(sceneId);
            scene.setType(sceneDetail.getType());
            scene.setTitle(sceneDetail.getSceneName());
            scene.setDesc(sceneDetail.getSceneDesc());
            scene.setUrl(sceneDetail.getUrl());
            scene.setWaitType(sceneDetail.getWaitType());
            scene.setWaitTime(sceneDetail.getWaitTime());
            scene.setCreateBy(sceneDetail.getCreateBy());
            return scene;
        }

    }

    // 如果是批量保存步骤的时候，直接同时对步骤进行更新
}
