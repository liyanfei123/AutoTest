package com.testframe.autotest.service;

import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;

public interface SceneExecuteService {

    public void executeScene(Long sceneId, Integer browserType);

    public void executeSet(Long setId, Integer browserType);

    public SeleniumRunEvent generateEvent(Long setRecordId, Long sceneId, Integer type);

}
