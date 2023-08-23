package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.ExecuteCmd;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;

public interface SceneExecuteService {

    public void executeV2(ExecuteCmd executeCmd);

    public void executeScene(Long sceneId, Integer browserType);

    public void executeSet(Long setId, Integer browserType);

    public SeleniumRunEvent generateEvent(Long setRecordId, Long sceneId, SceneSetConfigModel sceneSetConfigModel, Integer type);

}
