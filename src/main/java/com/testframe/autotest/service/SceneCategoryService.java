package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.SceneCategoryCmd;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.query.CategorySceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;

import java.util.List;

public interface SceneCategoryService {

    Integer updateSceneCategory(SceneCategoryCmd sceneCategoryCmd);

    Boolean deleteSceneCategory(Integer categoryId);

    SceneListVO queryScenesByCategoryId(CategorySceneQry categorySceneQry);

    List<ExeSetDto> querySetsByCategoryId(Integer categoryId);
}
