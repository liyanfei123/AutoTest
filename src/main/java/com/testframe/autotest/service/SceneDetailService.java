package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.meta.vo.SceneDetailVo;
import com.testframe.autotest.meta.vo.SceneRelListVO;

import java.util.List;

public interface SceneDetailService {

    public Long create(SceneCreateCmd sceneCreateCmd);

    public Boolean update(SceneUpdateCmd sceneUpdateCmd);

    public SceneDetailVo query(Long sceneId);

    public Long sceneCopy(Long sceneId);

    public Boolean deleteScene(Long sceneId);

    public List<Long> moveScene(List<Long> sceneId, Integer oldCategoryId, Integer newCategoryId);

    SceneRelListVO sceneRels(Long sceneId);

}
