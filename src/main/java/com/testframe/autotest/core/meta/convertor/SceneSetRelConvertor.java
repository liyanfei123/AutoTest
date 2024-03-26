package com.testframe.autotest.core.meta.convertor;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.SetMemTypeEnum;
import com.testframe.autotest.core.meta.Do.SceneSetRelDo;
import com.testframe.autotest.core.meta.po.SceneSetRel;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SceneSetRelConvertor {

    public SceneSetRelDo PoToDo(SceneSetRel sceneSetRel) {
        SceneSetRelDo sceneSetRelDo = new SceneSetRelDo();
        sceneSetRelDo.setRelId(sceneSetRel.getId());
        sceneSetRelDo.setSetId(sceneSetRel.getSetId());
        sceneSetRelDo.setSceneId(sceneSetRel.getSceneId());
        sceneSetRelDo.setStepId(sceneSetRel.getStepId());
        sceneSetRelDo.setType(sceneSetRel.getType());
        sceneSetRelDo.setExtInfo(sceneSetRel.getExtInfo());
        sceneSetRelDo.setStatus(sceneSetRel.getStatus());
        sceneSetRelDo.setSort(sceneSetRel.getSort());
        sceneSetRelDo.setCreateBy(sceneSetRel.getCreateBy());
        sceneSetRelDo.setUpdateTime(sceneSetRel.getUpdateTime());
        return sceneSetRelDo;
    }

    public SceneSetRel DoToPo(SceneSetRelDo sceneSetRelDo) {
        SceneSetRel sceneSetRel = new SceneSetRel();
        sceneSetRel.setId(sceneSetRelDo.getRelId());
        sceneSetRel.setSetId(sceneSetRelDo.getSetId());
        sceneSetRel.setSceneId(sceneSetRelDo.getSceneId());
        sceneSetRel.setStepId(sceneSetRelDo.getStepId());
        sceneSetRel.setType(sceneSetRelDo.getType());
        sceneSetRel.setExtInfo(sceneSetRelDo.getExtInfo());
        sceneSetRel.setStatus(sceneSetRelDo.getStatus());
        sceneSetRel.setSort(sceneSetRelDo.getSort());
        sceneSetRel.setCreateBy(sceneSetRelDo.getCreateBy());
        return sceneSetRel;
    }

    public SceneSetRelDo DtoToDo(SceneSetRelSceneDto sceneSetRelSceneDto) {
        SceneSetRelDo sceneSetRelDo = new SceneSetRelDo();
        sceneSetRelDo.setSetId(sceneSetRelSceneDto.getSetId());
        sceneSetRelDo.setSceneId(sceneSetRelSceneDto.getSceneId());
        sceneSetRelDo.setStatus(sceneSetRelSceneDto.getStatus());
        sceneSetRelDo.setType(SetMemTypeEnum.SCENE.getType());
        sceneSetRelDo.setExtInfo(JSON.toJSONString(sceneSetRelSceneDto.getSceneSetConfigModel()));
        sceneSetRelDo.setSort(sceneSetRelSceneDto.getSort());
        sceneSetRelDo.setCreateBy(sceneSetRelSceneDto.getCreateBy());
        return sceneSetRelDo;
    }


    public SceneSetRelDo DtoToDo(SceneSetRelStepDto sceneSetRelStepDto) {
        SceneSetRelDo sceneSetRelDo = new SceneSetRelDo();
        sceneSetRelDo.setRelId(null);
        sceneSetRelDo.setSetId(sceneSetRelStepDto.getSetId());
        sceneSetRelDo.setStepId(sceneSetRelStepDto.getStepId());
        sceneSetRelDo.setStatus(sceneSetRelStepDto.getStatus());
        sceneSetRelDo.setType(SetMemTypeEnum.SCENE.getType());
        sceneSetRelDo.setSort(sceneSetRelStepDto.getSort());
        sceneSetRelDo.setCreateBy(sceneSetRelStepDto.getCreateBy());
        return sceneSetRelDo;
    }

    public SceneSetRelStepDto DoToDto(SceneSetRelDo sceneSetRelDo) {
        SceneSetRelStepDto sceneSetRelStepDto = new SceneSetRelStepDto();
        sceneSetRelStepDto.setSetId(sceneSetRelDo.getSetId());
        sceneSetRelStepDto.setStepId(sceneSetRelDo.getSceneId());
        sceneSetRelStepDto.setStatus(sceneSetRelDo.getStatus());
        sceneSetRelStepDto.setSort(sceneSetRelDo.getSort());
        sceneSetRelStepDto.setCreateBy(sceneSetRelDo.getCreateBy());
        return sceneSetRelStepDto;
    }
}
