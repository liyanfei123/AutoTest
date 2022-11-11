package com.testframe.autotest.meta.validator;


import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 场景步骤检验
@Component
@Slf4j
public class StepValidator {

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    // 验证场景创建参数是否正确
    public void checkStepUpdate(StepUpdateCmd stepUpdateCmd) {
        if (stepUpdateCmd.getName() == null || stepUpdateCmd.getName().equals("")) {
            throw new AutoTestException("步骤name不能为空");
        } else if (stepUpdateCmd.getStepInfo() == null || stepUpdateCmd.getStepInfo().equals("")) {
            throw new AutoTestException("步骤信息不能为空");
        } else if (stepUpdateCmd.getSceneId() == null) {
            throw new AutoTestException("场景id不能为空");
        }
        // 检验执行状态参数是否正确
        if (StepStatusEnum.getByType(stepUpdateCmd.getStatus()) == null) {
            throw new AutoTestException("步骤状态错误");
        }
    }

    // 检验当前步骤id是否关联了场景，或者当前步骤是否存在
    public void checkStepId(Long stepId) {
        if (stepId == null) {
            throw new AutoTestException("步骤id不能为空或输入错误");
        }
        if (stepDetailRepository.queryStepById(stepId) == null) {
            throw new AutoTestException("步骤id不存在");
        }
        // 查找该步骤关联的场景id
        if (sceneStepRepository.queryByStepId(stepId) == null) {
            throw new AutoTestException("当前步骤id未与任何场景绑定, 不支持删除");
        }
    }




}
