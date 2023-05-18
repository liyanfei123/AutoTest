package com.testframe.autotest.service;


import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.command.SceneSetRelCmd;
import com.testframe.autotest.meta.command.SceneSetRelDelCmd;
import com.testframe.autotest.meta.command.SceneSetRelTopCmd;
import com.testframe.autotest.meta.vo.SetRelListVo;
import com.testframe.autotest.meta.vo.SetStepListVo;

/**
 * 执行集
 */
public interface SceneSetService {

    Long updateSceneSet(ExeSetUpdateCmd exeSetUpdateCmd);

    Boolean deleteSceneSet(Long setId);

    Boolean deleteSceneSetRel(SceneSetRelDelCmd sceneSetRelDelCmd);

    Boolean updateSceneSetRel(SceneSetRelCmd sceneSetRelCmd);

    Boolean topSetSceneOrStepRel(SceneSetRelTopCmd sceneSetRelTopCmd);

    SetRelListVo querySetScenes((Long setId, Integer status, Integer type, Integer page, Integer pageSize));



}
