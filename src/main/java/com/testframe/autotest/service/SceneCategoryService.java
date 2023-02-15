package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.SceneCategoryCmd;

public interface SceneCategoryService {

    Integer updateSceneCategory(SceneCategoryCmd sceneCategoryCmd);

    Boolean deleteSceneCategory(Integer categoryId);
}
