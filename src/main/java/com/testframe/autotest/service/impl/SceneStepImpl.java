package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.service.SceneStepService;
import com.testframe.autotest.validator.StepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class SceneStepImpl implements SceneStepService {

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private StepOrderImpl stepOrderImpl;

    @Override
    public List<Long> updateSceneStep(Long sceneId, List<Step> steps) {
        // 查询当前场景下关联的步骤
        HashMap<Long,SceneStepRel> existedStepMap = new HashMap<>();
        List<Long> stepIds = new ArrayList<>(); // 步骤执行顺序
        SceneStepRel sceneStepRel;

        List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        if (!sceneStepRels.isEmpty()) {
            sceneStepRels.stream().forEach(e -> existedStepMap.put(e.getStepId(), e));
        }
        for (Step step : steps) {
                Long stepId = step.getStepId();
                if (stepId == null) {
                    // 新增步骤
                    log.info("[SceneStepInterImpl:updateSceneStep] add step in sceneId {}, step = {}", sceneId, JSON.toJSONString(step));
                    try {
                        stepId = stepDetailRepository.saveStep(step);
                        sceneStepRepository.saveSceneStep(SceneStepRel.build(sceneId, step));
                    } catch (Exception e) {
                        log.error("[SceneStepInterImpl:updateSceneStep] update step error, stepId = {}, reason = {}", sceneId, e.getStackTrace());
                        throw new AutoTestException("当前场景下新增步骤失败");
                    }
                } else {
                    try {
                        // 更新步骤
                        sceneStepRel = existedStepMap.get(stepId);
                        log.info("[SceneStepInterImpl:updateSceneStep] update step in sceneId-stepId {}-{}, sceneStepRel = {}", sceneId, step.getStepId(), JSON.toJSONString(sceneStepRel));
                        stepDetailRepository.update(step);
                        sceneStepRel.setStatus(step.getStatus());
                        sceneStepRepository.updateSceneStep(sceneStepRel);
                        stepIds.add(stepId);
                        existedStepMap.remove(stepId);
                    } catch (Exception e) {
                        log.error("[SceneStepInterImpl:updateSceneStep] update step error, stepId = {}, reason = {}", stepId, e.getStackTrace());
                        throw new AutoTestException("当前场景下已有步骤更新失败");
                    }
                }
                stepIds.add(stepId);
        }
        // 把未进行操作的步骤给删除掉
        removeSceneStepRel(existedStepMap);
        return stepIds;
    }

    @Override
    public void removeSceneStepRel(Long stepId) {
        try {
            stepValidator.checkStepId(stepId);
            SceneStepRel sceneStepRel = sceneStepRepository.queryByStepId(stepId);
            sceneStepRel.setIsDelete(1);
            sceneStepRepository.updateSceneStep(sceneStepRel);
            Long sceneId = sceneStepRel.getSceneId();
            stepOrderImpl.removeStepId(sceneId, stepId);
        } catch (AutoTestException e) {
            log.error("[SceneStepInterImpl:removeSceneStepRel] remove step {} error, reason = {}", stepId, e.getStackTrace());
            throw new AutoTestException(e.getMessage());
        }
        // 获取当前场景下的执行步骤顺序，对其进行修改
    }

    @Override
    public List<Long> queryStepBySceneId(Long sceneId) {
        try {
            log.info("[SceneStepImpl:queryStepBySceneId] query steps in scene {}", sceneId);
            List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            List<Long> stepIds = new ArrayList<>(sceneStepRels.size());
            sceneStepRels.forEach(sceneStepRel -> stepIds.add(sceneStepRel.getStepId()));
            log.info("[SceneStepImpl:queryStepBySceneId] query steps in scene {}, steps {}", sceneId, JSON.toJSONString(stepIds));
            return stepIds;
        } catch (Exception e) {
            log.error("[SceneStepImpl:queryStepBySceneId] query steps in scene {} error, reason = ", sceneId, e);
        }
        return null;
    }


    // 移除步骤场景绑定关系
    private void removeSceneStepRel(HashMap<Long, SceneStepRel> sceneStepMap) {
        if (!sceneStepMap.isEmpty()) {
            for (SceneStepRel sceneStepRel : sceneStepMap.values()) {
                sceneStepRel.setIsDelete(1);
                sceneStepRepository.updateSceneStep(sceneStepRel);
            }
        }
    }
}
