package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.meta.dto.SceneDetailInfo;

public interface SceneDetailService {

    public Long create(SceneCreateCmd sceneCreateCmd);

    public Boolean update(SceneUpdateCmd sceneUpdateCmd);

    public SceneDetailInfo query(Long sceneId);

}
