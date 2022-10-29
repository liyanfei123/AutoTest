package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.service.SceneStepInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class SceneStepInterImpl implements SceneStepInter {

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

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

    @Override
    public void updateSceneStep(Long sceneId, HashMap<Long, Step> steps) {
        // 查询当前场景下关联的步骤
        List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        // 把已有的进行更新，新增的内容单独进行新增
        for (SceneStepRel sceneStepRel : sceneStepRels) {
            try {
                if (sceneStepRel.getIsDelete() > 0) {
                    continue;
                }
                log.info("[SceneStepInterImpl:updateSceneStep] update step in sceneId-stepId {}-{}, sceneStepRel = {}", sceneId, sceneStepRel.getStepId(), JSON.toJSONString(sceneStepRel));
                Step step = steps.get(sceneStepRel.getStepId());
                stepDetailRepository.update(step);
                sceneStepRel.setStatus(step.getStatus());
                sceneStepRepository.updateSceneStep(sceneStepRel);
                steps.remove(sceneStepRel.getStepId());
            } catch (Exception e) {
                log.error("[SceneStepInterImpl:updateSceneStep] update step error, stepId = {}, reason = {}", sceneStepRel.getStepId(), e.getStackTrace());
                throw new AutoTestException("当前场景下已有步骤更新失败");
            }
        }
        if (!steps.isEmpty()) {
            // 还有内容没有被操作过，代表是新增的步骤
            steps.forEach((stepId, step) -> {
                log.info("[SceneDetailImpl:update] add step in sceneId {}, step = {}", sceneId, JSON.toJSONString(step));
                try {
                    stepDetailRepository.saveStep(step);
                    sceneStepRepository.saveSceneStep(SceneStepRel.build(sceneId, step));
                } catch (Exception e) {
                    log.error("[SceneStepInterImpl:updateSceneStep] update step error, stepId = {}, reason = {}", sceneId, e.getStackTrace());
                    throw new AutoTestException("当前场景下新增步骤失败");
                }
            });
        }
    }
}
