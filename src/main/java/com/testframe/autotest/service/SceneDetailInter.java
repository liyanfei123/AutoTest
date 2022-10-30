package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;

public interface SceneDetailInter {

    public Long create(SceneCreateCmd sceneCreateCmd);

    public Boolean update(SceneUpdateCmd sceneUpdateCmd);

}
