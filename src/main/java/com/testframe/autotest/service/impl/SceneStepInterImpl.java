package com.testframe.autotest.service.impl;

import com.testframe.autotest.service.SceneStepInter;

public class SceneStepInterImpl implements SceneStepInter {


    @Override
    public Boolean stepSave() {
        // 如果当前步骤id存在，则是更新步骤，否则是创建步骤
        // 创建步骤时，需要和场景进行关联
        return null;
    }

    @Override
    public Boolean batchStepSave() {
        return null;
    }
}
