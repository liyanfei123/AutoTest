package com.testframe.autotest.service;

import com.testframe.autotest.meta.bo.Step;

import java.util.HashMap;
import java.util.List;

public interface SceneStepService {

    public List<Long> updateSceneStep(Long sceneId, List<Step> steps);

    public void removeSceneStepRel(Long step);

    public List<Long> queryStepBySceneId(Long sceneId);
}
