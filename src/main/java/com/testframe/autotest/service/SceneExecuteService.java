package com.testframe.autotest.service;

import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;

public interface SceneExecuteService {

    public void execute(Long sceneId, Integer browserType);

    public SeleniumRunEvent generateEvent(Long sceneId, Integer type);

}
