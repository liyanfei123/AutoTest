package com.testframe.autotest.core.repository;


import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.meta.convertor.SceneStepConverter;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.core.repository.dao.SceneStepDao;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepRel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SceneStepRepository {

    @Autowired
    private SceneStepDao sceneStepDao;

    @Autowired
    private SceneStepConverter sceneStepConverter;

    @Transactional(rollbackFor = Exception.class)
    public Boolean saveSceneStep(SceneStepRel sceneStepRel) {
        SceneStep sceneStep = sceneStepConverter.toPO(sceneStepRel);
        return sceneStepDao.saveSceneStep(sceneStep);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchSaveSceneStep(List<SceneStepRel> sceneStepRels) {
        List<SceneStep> sceneSteps = sceneStepRels.stream().map(sceneStepConverter::toPO).collect(Collectors.toList());
//        List<SceneStep> sceneSteps = sceneStepRels.stream().map(sceneStepRel -> {
//                    return sceneStepConverter.toPO(sceneStepRel);
//                }).collect(Collectors.toList());
       try {
           for (SceneStep sceneStep : sceneSteps) {
               sceneStepDao.saveSceneStep(sceneStep);
           }
           return true;
       } catch (Exception e) {
           log.error("[SceneStepRepository:batchSaveSceneStep] save scene-steps error, reason ={}", e.getMessage());
           return false;
       }
    }

    public Boolean updateSceneStep(SceneStepRel sceneStepRel) {
        SceneStep sceneStep = sceneStepConverter.toPO(sceneStepRel);
        return sceneStepDao.updateSceneStep(sceneStep);
    }

    public List<SceneStepRel> querySceneStepsBySceneId(Long sceneId) {
        List<SceneStep> sceneSteps = sceneStepDao.queryBySceneId(sceneId);
        List<SceneStepRel> sceneStepRels = new ArrayList<>();
        sceneSteps.forEach(sceneStep -> {
            SceneStepRel sceneStepRel = build(sceneStep);
            sceneStepRels.add(sceneStepRel);
        });
        return sceneStepRels;
    }

    public SceneStepRel queryByStepId(Long stepId) {
        if (stepId == null) {
            return null;
        }
        SceneStep sceneStep = sceneStepDao.queryByStepId(stepId);
        return build(sceneStep);
    }


    private SceneStepRel build(SceneStep sceneStep) {
        if (sceneStep == null) {
            return null;
        }
        SceneStepRel sceneStepRel = new SceneStepRel();
        sceneStepRel.setId(sceneStep.getId());
        sceneStepRel.setStepId(sceneStep.getStepId());
        sceneStepRel.setStatus(sceneStepRel.getStatus());
        sceneStepRel.setIsDelete(sceneStepRel.getIsDelete());
        return sceneStepRel;
    }


}
