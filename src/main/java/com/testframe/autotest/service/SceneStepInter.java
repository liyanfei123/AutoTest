package com.testframe.autotest.service;

import com.testframe.autotest.meta.bo.Step;

import java.util.HashMap;
import java.util.List;

public interface SceneStepInter {

    public Boolean stepSave();

    // 批量保存
    public Boolean batchStepSave();


    public void updateSceneStep(Long sceneId, HashMap<Long, Step> steps);
}
