package com.testframe.autotest.meta.validator;

import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.service.SceneStepService;
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
        Scene scene = sceneDetailRepository.querySceneById(sceneId);
        if (scene == null) {
            throw new AutoTestException("当前场景不存在");
        }
        List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        List<Integer> status = sceneStepRels.stream().map(SceneStepRel::getStatus).collect(Collectors.toList());
        if (status.isEmpty() || !status.contains(StepStatusEnum.OPEN.getType())) {
            throw new AutoTestException("当前场景下无可执行步骤");
        }
    }
}
