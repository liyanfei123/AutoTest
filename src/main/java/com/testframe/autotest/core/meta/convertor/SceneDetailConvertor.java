package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.meta.bo.Scene;
import org.springframework.stereotype.Component;

@Component
public class SceneDetailConvertor {

    public SceneDetail toPO(Scene scene) {
        SceneDetail sceneDetail = new SceneDetail();
        sceneDetail.setId(scene.getId());
        sceneDetail.setSceneName(scene.getTitle());
        sceneDetail.setSceneDesc(scene.getDesc());
        sceneDetail.setType(scene.getType());
        sceneDetail.setUrl(scene.getUrl());
        sceneDetail.setWaitType(scene.getWaitType());
        sceneDetail.setWaitTime(scene.getWaitTime());
        sceneDetail.setIsDelete(scene.getIsDelete());
        sceneDetail.setCreateBy(scene.getCreateBy());
        return sceneDetail;
    }

}
