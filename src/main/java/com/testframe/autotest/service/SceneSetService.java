package com.testframe.autotest.service;


import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.command.SceneSetRelCmd;
import com.testframe.autotest.meta.command.SceneSetRelDelCmd;
import com.testframe.autotest.meta.command.SceneSetRelTopCmd;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.vo.SetRelListVo;

import java.util.List;

/**
 * 执行集
 */
public interface SceneSetService {

    Long updateSceneSet(ExeSetUpdateCmd exeSetUpdateCmd);

    ExeSetDto querySet(Long setId);

    List<ExeSetDto> queryRelByStepIdOrSceneId(Long stepId, Long sceneId);

    Boolean deleteSceneSet(Long setId);

    Boolean deleteSceneSetRel(SceneSetRelDelCmd sceneSetRelDelCmd);

    Boolean updateSceneSetRel(SceneSetRelCmd sceneSetRelCmd);

    Boolean topSetSceneOrStepRel(SceneSetRelTopCmd sceneSetRelTopCmd);

    SetRelListVo querySetRels(Long setId, Integer status, Integer type, Integer page, Integer pageSize);

    Boolean moveCategoryId(Long setId, Integer oldCategoryId, Integer newCategoryId);

}
