package com.testframe.autotest.service;

import com.testframe.autotest.meta.query.SceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;

import java.util.List;

public interface SceneListInter {

    public SceneListVO queryScenes(SceneQry sceneQry);

    public Boolean deleteScene(Long sceneId);

}
