package com.testframe.autotest.service;

import com.testframe.autotest.command.SceneCreateCmd;
import com.testframe.autotest.command.SceneUpdateCmd;

import java.util.List;

public interface SceneDetailInter {

    public Long create(SceneCreateCmd sceneCreateCmd);

    public Boolean update(SceneUpdateCmd sceneUpdateCmd);

}
