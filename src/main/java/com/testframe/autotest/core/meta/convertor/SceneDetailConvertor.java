package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
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

    public SceneDetail DoToPO(SceneDetailDo sceneDetailDo) {
        SceneDetail sceneDetail = new SceneDetail();
        sceneDetail.setId(sceneDetailDo.getSceneId());
        sceneDetail.setSceneName(sceneDetailDo.getSceneName());
        sceneDetail.setSceneDesc(sceneDetailDo.getSceneDesc());
        sceneDetail.setType(sceneDetailDo.getType());
        sceneDetail.setUrl(sceneDetailDo.getUrl());
        sceneDetail.setWaitType(sceneDetailDo.getWaitType());
        sceneDetail.setWaitTime(sceneDetailDo.getWaitTime());
        sceneDetail.setIsDelete(sceneDetailDo.getIsDelete());
        sceneDetail.setCreateBy(sceneDetailDo.getCreateBy());
        return sceneDetail;
    }

    public SceneDetailDo PoToDo(SceneDetail sceneDetail) {
        SceneDetailDo sceneDetailDo = new SceneDetailDo();
        sceneDetailDo.setSceneId(sceneDetail.getId());
        sceneDetailDo.setSceneName(sceneDetail.getSceneName());
        sceneDetailDo.setSceneDesc(sceneDetail.getSceneDesc());
        sceneDetailDo.setType(sceneDetail.getType());
        sceneDetailDo.setUrl(sceneDetail.getUrl());
        sceneDetailDo.setWaitType(sceneDetail.getWaitType());
        sceneDetailDo.setWaitTime(sceneDetail.getWaitTime());
        sceneDetailDo.setIsDelete(sceneDetail.getIsDelete());
        sceneDetailDo.setCreateBy(sceneDetail.getCreateBy());
        return sceneDetailDo;
    }

    public SceneDetailDo DtoToDo(SceneDetailDto sceneDetailDto) {
        SceneDetailDo sceneDetailDo = new SceneDetailDo();
        sceneDetailDo.setSceneId(sceneDetailDto.getSceneId());
        sceneDetailDo.setSceneName(sceneDetailDto.getSceneName());
        sceneDetailDo.setSceneDesc(sceneDetailDto.getSceneDesc());
        sceneDetailDo.setType(sceneDetailDto.getType());
        sceneDetailDo.setUrl(sceneDetailDto.getUrl());
        sceneDetailDo.setWaitType(sceneDetailDto.getWaitType());
        sceneDetailDo.setWaitTime(sceneDetailDto.getWaitTime());
        sceneDetailDo.setCreateBy(sceneDetailDto.getCreateBy());
        return sceneDetailDo;
    }

}
