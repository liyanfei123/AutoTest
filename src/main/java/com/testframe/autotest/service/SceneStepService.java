package com.testframe.autotest.service;

import com.testframe.autotest.meta.bo.Step;

import java.util.HashMap;
import java.util.List;

public interface SceneStepService {

    public List<Long> updateSceneStep(Long sceneId, List<Step> steps);

    public void removeSceneStepRel(Long stepId);

    public void removeSceneStepRel(List<Long> stepIds);

    public void removeSceneStepRelWithOrder(Long step);

    public List<Long> queryStepBySceneId(Long sceneId);

    public void batchSaveSceneStep(List<Long> stepIds, Long sceneId);

}
