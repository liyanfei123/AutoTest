package com.testframe.autotest.meta.validator;

import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExecuteValidator {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    public void validate(Long sceneId) {
        SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(sceneId);
        if (sceneDetailDo == null) {
            throw new AutoTestException("当前场景不存在");
        }
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        List<Integer> status = sceneStepRelDos.stream().map(sceneStepRelDo -> sceneStepRelDo.getStatus())
                .collect(Collectors.toList());
        if (status.isEmpty() || !status.contains(StepStatusEnum.OPEN.getType())) {
            throw new AutoTestException("当前场景下无可执行步骤");
        }
    }
}
