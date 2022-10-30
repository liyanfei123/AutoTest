package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.service.SceneCopyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/10/30 20:47
 * @author: lyf
 */
@Slf4j
@Service
public class SceneCopyServiceImpl implements SceneCopyService {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Override
    public Long copy(Long sceneId) {
        Scene scene = sceneDetailRepository.querySceneById(sceneId);
        if (scene == null) {
            throw new AutoTestException("当前场景不存在，不支持复制，请重新选择");
        }
        // 复制场景信息
        scene.setId(null);
        Long newSceneId = sceneDetailRepository.saveScene(scene);

        // 复制场景步骤
        List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        List<Long> stepIds = sceneStepRels.stream().map(SceneStepRel::getStepId).collect(Collectors.toList());

        sceneStepRels.forEach(sceneStepRel -> {
            // 将单个步骤进行保存
            stepDetailRepository.batchSaveStep(null);
        });


        return newSceneId;
    }
}
