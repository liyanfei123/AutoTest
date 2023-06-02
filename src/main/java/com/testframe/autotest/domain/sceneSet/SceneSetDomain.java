package com.testframe.autotest.domain.sceneSet;

import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;

import java.util.List;

public interface SceneSetDomain {

    public Long updateSceneSet(ExeSetDto exeSetDto);

    public Boolean deleteSceneSet(Long setId);

    public List<Long> updateSceneSetRel(List<SceneSetRelSceneDto> sceneSetRelSceneDtos, List<SceneSetRelStepDto> sceneSetRelStepDtos);

    public Boolean deleteSceneSetRel(Long setId, Long sceneId, Long stepId);

    public SceneSetBo querySetBySetId(Long setId, Integer type, Integer status, PageQry pageQry);

    List<ExeSetDto> queryRelByStepIdOrSceneId(Long stepId, Long sceneId);

}
