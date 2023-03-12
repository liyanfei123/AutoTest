package com.testframe.autotest.core.repository;

import com.testframe.autotest.cache.ao.SceneStepRelCache;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.meta.convertor.SceneStepConverter;
import com.testframe.autotest.core.meta.convertor.StepOrderConverter;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.dao.SceneStepDao;
import com.testframe.autotest.core.repository.dao.StepOrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SceneStepRepository {

    @Autowired
    private SceneStepDao sceneStepDao;

    @Autowired
    private StepOrderDao stepOrderDao;

    @Autowired
    private SceneStepRelCache sceneStepRelCache;

    @Autowired
    private SceneStepConverter sceneStepConverter;

    @Autowired
    private StepOrderConverter stepOrderConverter;

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateSceneStep(List<SceneStepRelDo> sceneStepRelDos) {
        List<SceneStep> sceneSteps = sceneStepRelDos.stream().map(sceneStepConverter::DoToPO).collect(Collectors.toList());
        try {
            for (SceneStep sceneStep : sceneSteps) {
                sceneStepDao.updateSceneStep(sceneStep);
            }
            sceneStepRelCache.clearSceneStepRels(sceneSteps.get(0).getSceneId());
            return true;
        } catch (Exception e) {
            log.error("[SceneStepRepository:batchUpdateSceneStep] update scene-steps error, reason ={}", e.getMessage());
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSceneStep(SceneStepRelDo sceneStepRelDo) {
        SceneStep sceneStep = sceneStepConverter.DoToPO(sceneStepRelDo);
        if (!sceneStepDao.updateSceneStep(sceneStep)) {
            return false;
        }
        sceneStepRelCache.clearSceneStepRel(sceneStep.getSceneId(), sceneStep.getStepId());
        return true;
    }

    public SceneStepRelDo queryByStepIdAndSceneId(Long stepId, Long sceneId) {
        SceneStep sceneStep = sceneStepDao.queryStepByStepIdAndSceneId(stepId, sceneId);
        if (sceneStep == null) {
            return null;
        }
        SceneStepRelDo sceneStepRelDo = sceneStepConverter.PoToDo(sceneStep);
        return sceneStepRelDo;
    }

    public List<SceneStepRelDo> querySceneStepsBySceneId(Long sceneId) {
        List<SceneStep> sceneSteps = sceneStepDao.queryBySceneId(sceneId);
        if (CollectionUtils.isEmpty(sceneSteps)) {
            return Collections.EMPTY_LIST;
        }
        List<SceneStepRelDo> sceneStepRelDos = new ArrayList<>();
        sceneSteps.forEach(sceneStep -> {
            SceneStepRelDo sceneStepRelDo = sceneStepConverter.PoToDo(sceneStep);
            sceneStepRelDos.add(sceneStepRelDo);
        });
        return sceneStepRelDos;
    }

    public SceneStepRelDo queryByStepId(Long stepId) {
        SceneStep sceneStep = sceneStepDao.queryByStepId(stepId);
        if (sceneStep == null) {
            return null;
        }
        return sceneStepConverter.PoToDo(sceneStep);
    }

    public List<SceneStepRelDo> queryByStepIds(List<Long> stepIds) {
        List<SceneStep> sceneStep = sceneStepDao.queryByStepIds(stepIds);
        if (sceneStep.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SceneStepRelDo> sceneStepRelDos = sceneStep.stream().map(sceneStepConverter::PoToDo)
                .collect(Collectors.toList());
        return sceneStepRelDos;
    }



}
