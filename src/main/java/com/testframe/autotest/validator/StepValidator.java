package com.testframe.autotest.validator;


import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.command.StepCreateCmd;
import com.testframe.autotest.command.StepUpdateCmd;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 场景步骤检验
@Component
@Slf4j
public class StepValidator {

    // 验证场景创建参数是否正确
    public void checkStepUpdate(StepUpdateCmd stepUpdateCmd) {
        if (stepUpdateCmd.getName() == null) {
            throw new AutoTestException("步骤name不能为空");
        } else if (stepUpdateCmd.getStepInfo() == null) {
            throw new AutoTestException("步骤信息不能为空");
        } else if (stepUpdateCmd.getSceneId() == null) {
            throw new AutoTestException("场景id不能为空");
        }
        // 检验执行状态参数是否正确
        if (StepStatusEnum.getByType(stepUpdateCmd.getStatus()) == null) {
            throw new AutoTestException("步骤状态错误");
        }
    }


}
