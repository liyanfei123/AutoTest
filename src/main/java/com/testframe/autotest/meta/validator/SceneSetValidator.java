package com.testframe.autotest.meta.validator;

import com.testframe.autotest.core.enums.ExeOrderEnum;
import com.testframe.autotest.core.enums.OpenStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.repository.ExeSetRepository;
import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SceneSetValidator {

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Autowired
    private SceneValidator sceneValidator;

    @Autowired
    private StepValidator stepValidator;

    public Boolean checkSceneSetUpdate(ExeSetUpdateCmd exeSetUpdateCmd) {
        if (!OpenStatusEnum.getTypes().contains(exeSetUpdateCmd.getStatus())) {
            throw new AutoTestException("请输入正确的状态信息");
        } if (exeSetUpdateCmd.getSetId() != null && exeSetUpdateCmd.getSetId() <= 0L) {
            throw new AutoTestException("请输入正确的执行集合id");
        }
        return true;
    }

    public void checkSceneSetValid(Long setId) {
        ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(setId);
        if (exeSetDo == null) {
            throw new AutoTestException("执行集id错误");
        }
    }

    public void checkRelValid(List<SceneSetRelSceneDto> sceneSetRelSceneDtos,
                                      List<SceneSetRelStepDto> sceneSetRelStepDtos) {
        for (SceneSetRelSceneDto sceneSetRelSceneDto : sceneSetRelSceneDtos) {
            if (!OpenStatusEnum.getTypes().contains(sceneSetRelSceneDto.getStatus())) {
                throw new AutoTestException("关联场景状态错误");
            }
            if (!ExeOrderEnum.getTypes().contains(sceneSetRelSceneDto.getSort())) {
                throw new AutoTestException("关联场景执行顺序错误");
            }
            sceneValidator.sceneIsExist(sceneSetRelSceneDto.getSceneId());
        }
        for (SceneSetRelStepDto sceneSetRelStepDto : sceneSetRelStepDtos) {
            if (!OpenStatusEnum.getTypes().contains(sceneSetRelStepDto.getStatus())) {
                throw new AutoTestException("关联步骤状态错误");
            }
            if (!ExeOrderEnum.getTypes().contains(sceneSetRelStepDto.getSort())) {
                throw new AutoTestException("关联步骤执行顺序错误");
            }
            stepValidator.checkStepId(sceneSetRelStepDto.getStepId());
        }
    }
}
