package com.testframe.autotest.meta.validator;


import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.domain.sceneStep.SceneStepDomain;
import com.testframe.autotest.meta.command.StepStatusUpdateCmd;
import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.model.StepInfoModel;
import com.testframe.autotest.ui.enums.OperateTypeEnum;
import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 场景步骤检验
@Component
@Slf4j
public class StepValidator {

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepDomain sceneStepDomain;

    public void checkStepSave(List<StepUpdateCmd> stepUpdateCmds) {
        for (StepUpdateCmd stepUpdateCmd : stepUpdateCmds) {
            if (stepUpdateCmd.getSonSceneId() == 0 || stepUpdateCmd.getSonSceneId() == null) {
                checkStepSaveNoSon(stepUpdateCmd);
            } else {
                checkStepIsSon(stepUpdateCmd);
            }
        }
    }

    public void checkStepUpdate(List<StepUpdateCmd> stepUpdateCmds) {
        for (StepUpdateCmd stepUpdateCmd : stepUpdateCmds) {
            if (stepUpdateCmd.getSonSceneId() == 0 || stepUpdateCmd.getSonSceneId() == null) {
                checkStepUpdateNoSon(stepUpdateCmd);
            }
            else {
                checkStepIsSon(stepUpdateCmd);
            }
        }
    }

    // 验证单独场景
    private void checkStepSaveNoSon(StepUpdateCmd stepUpdateCmd) {
        if (stepUpdateCmd.getStepId() > 0) {
            throw new AutoTestException("不可保存已有步骤");
        }
        // 单独的步骤、非子场景
        if (stepUpdateCmd.getName() == null || stepUpdateCmd.getName().equals("")) {
            throw new AutoTestException("步骤名称不能为空");
        }
        if (stepUpdateCmd.getStepInfo() == null || stepUpdateCmd.getStepInfo().equals("")) {
            throw new AutoTestException("步骤信息不能为空");
        }
        // 检验执行状态参数是否正确
        if (stepUpdateCmd.getStatus() == null || StepStatusEnum.getByType(stepUpdateCmd.getStatus()) == null) {
            throw new AutoTestException("步骤状态错误");
        }
        StepInfoModel stepInfoModel = JSON.parseObject(stepUpdateCmd.getStepInfo(), StepInfoModel.class);
        // 验证元素操作类型
        if (stepInfoModel.getOperateType() == OperateTypeEnum.OPERATE.getType() &&
                stepInfoModel.getOperateMode() == null) {
            throw new AutoTestException("请输入元素操作类型");
        }
        if (stepInfoModel.getOperateMode() != null &&
                OperateModeEnum.getByType(stepInfoModel.getOperateMode()) == null) {
            throw new AutoTestException("当前元素操作类型不被支持");
        }
        // 验证元素等待类型
        if (stepInfoModel.getOperateType() == OperateTypeEnum.WAIT.getType() &&
                stepInfoModel.getWaitMode() == null) {
            throw new AutoTestException("请输入元素等待类型");
        }
        if (stepInfoModel.getWaitMode() != null &&
                WaitModeEnum.getByType(stepInfoModel.getWaitMode()) == null) {
            throw new AutoTestException("当前元素操作类型不被支持");
        }
        // 验证元素检验类型
        if (stepInfoModel.getOperateType() == OperateTypeEnum.ASSERT.getType() &&
                stepInfoModel.getAssertMode() == null) {
            throw new AutoTestException("请输入元素检验类型");
        }
        if (stepInfoModel.getAssertMode() != null &&
                AssertModeEnum.getByType(stepInfoModel.getAssertMode()) == null) {
            throw new AutoTestException("当前元素检验类型不被支持");
        }
    }

    private void checkStepUpdateNoSon(StepUpdateCmd stepUpdateCmd) {
        if (stepUpdateCmd.getStepId() == null || stepUpdateCmd.getStepId() <= 0) {
            throw new AutoTestException("步骤id错误");
        }
        // 检验执行状态参数是否正确
        if (StepStatusEnum.getByType(stepUpdateCmd.getStatus()) == null) {
            throw new AutoTestException("步骤状态错误");
        }
        StepInfoModel stepInfoModel = JSON.parseObject(stepUpdateCmd.getStepInfo(), StepInfoModel.class);
        if (stepInfoModel.getOperateMode() != null &&
                OperateModeEnum.getByType(stepInfoModel.getOperateMode()) == null) {
            throw new AutoTestException("当前元素操作类型不被支持");
        }
        if (stepInfoModel.getWaitMode() != null &&
                WaitModeEnum.getByType(stepInfoModel.getWaitMode()) == null) {
            throw new AutoTestException("当前元素操作类型不被支持");
        }
        if (stepInfoModel.getAssertMode() != null &&
                AssertModeEnum.getByType(stepInfoModel.getAssertMode()) == null) {
            throw new AutoTestException("当前元素检验类型不被支持");
        }
    }
    // 验证单独场景为子场景
    public SceneDetailDo checkStepIsSon(StepUpdateCmd stepUpdateCmd) {
        // 判断是否存在循环引用
        checkRecycle(stepUpdateCmd.getSonSceneId());
        SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(stepUpdateCmd.getSonSceneId());
        if (sceneDetailDo == null || sceneDetailDo.getIsDelete() == 1) {
            throw new AutoTestException("子场景不存在");
        }
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(stepUpdateCmd.getSonSceneId());
        List<Integer> status = sceneStepRelDos.stream().map(sceneStepRel -> sceneStepRel.getStatus())
                .collect(Collectors.toList());
        if (status.isEmpty()) { // 无步骤
            return sceneDetailDo;
        }
        if (!status.contains(StepStatusEnum.OPEN.getType())) {
            throw new AutoTestException("子场景所有步骤均未开启");
        }
        return sceneDetailDo;
    }

    // 验证场景创建参数是否正确
//    public void checkStepCreate(StepUpdateCmd stepUpdateCmd) {
//        if (stepUpdateCmd.getName() == null || stepUpdateCmd.getName().equals("")) {
//            throw new AutoTestException("步骤name不能为空");
//        }
//        if (stepUpdateCmd.getSonSceneId() == 0 || stepUpdateCmd.getSonSceneId() == null) {
//            // 单独的步骤、非子场景
//            if (stepUpdateCmd.getStepInfo() == null || stepUpdateCmd.getStepInfo().equals("")) {
//                throw new AutoTestException("步骤信息不能为空");
//            }
//            // 检验执行状态参数是否正确
//            if (StepStatusEnum.getByType(stepUpdateCmd.getStatus()) == null) {
//                throw new AutoTestException("步骤状态错误");
//            }
//            StepInfoModel stepInfoModel = JSON.parseObject(stepUpdateCmd.getStepInfo(), StepInfoModel.class);
//            // 验证元素操作类型
//            if (stepInfoModel.getOperateType() == OperateTypeEnum.OPERATE.getType() &&
//                    stepInfoModel.getOperateMode() == null) {
//                throw new AutoTestException("请输入元素操作类型");
//            }
//            if (stepInfoModel.getOperateMode() != null &&
//                    OperateModeEnum.getByType(stepInfoModel.getOperateMode()) == null) {
//                throw new AutoTestException("当前元素操作类型不被支持");
//            }
//            // 验证元素等待类型
//            if (stepInfoModel.getOperateType() == OperateTypeEnum.WAIT.getType() &&
//                    stepInfoModel.getWaitMode() == null) {
//                throw new AutoTestException("请输入元素等待类型");
//            }
//            if (stepInfoModel.getWaitMode() != null &&
//                    WaitModeEnum.getByType(stepInfoModel.getWaitMode()) == null) {
//                throw new AutoTestException("当前元素操作类型不被支持");
//            }
//            // 验证元素检验类型
//            if (stepInfoModel.getOperateType() == OperateTypeEnum.ASSERT.getType() &&
//                    stepInfoModel.getAssertMode() == null) {
//                throw new AutoTestException("请输入元素检验类型");
//            }
//            if (stepInfoModel.getAssertMode() != null &&
//                    AssertModeEnum.getByType(stepInfoModel.getAssertMode()) == null) {
//                throw new AutoTestException("当前元素检验类型不被支持");
//            }
//        } else {
//            // 子场景判断
//            SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(stepUpdateCmd.getSonSceneId());
//            if (sceneDetailDo == null || sceneDetailDo.getIsDelete() == 1) {
//                throw new AutoTestException("子场景不存在");
//            }
//            List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(stepUpdateCmd.getSonSceneId());
//            List<Integer> status = sceneStepRelDos.stream().map(sceneStepRel -> sceneStepRel.getStatus())
//                    .collect(Collectors.toList());
//            if (!status.contains(StepStatusEnum.OPEN.getType())) {
//                throw new AutoTestException("子场景所有步骤均未开启");
//            }
//        }
//    }

    // 检验当前步骤id是否关联了场景，或者当前步骤是否存在
    public void checkStepId(Long stepId) {
        if (stepId == null) {
            throw new AutoTestException("步骤id不能为空或输入错误");
        }
        if (stepDetailRepository.queryStepById(stepId) == null) {
            throw new AutoTestException("步骤id不存在");
        }
    }

    public Boolean checkStepWithScene(List<Long> stepIds, Long sceneId) {
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.queryByStepIds(stepIds);
        if (sceneStepRelDos.size() != stepIds.size()) {
            return false;
        }
        sceneStepRelDos = sceneStepRelDos.stream().filter(sceneStepRelDo -> sceneStepRelDo.getSceneId() != sceneId)
                .collect(Collectors.toList());
        if (!sceneStepRelDos.isEmpty()) {
            return false;
        }
        return true;
    }

    public Boolean checkStepStatus(StepStatusUpdateCmd stepStatusUpdateCmd) {
        if (StepStatusEnum.getByType(stepStatusUpdateCmd.getStatus()) == null) {
            throw new AutoTestException("步骤状态错误");
        }
        return true;
    }

    private void checkRecycle(Long sceneId) {
        List<Long> sceneIds = new ArrayList<Long>(){{add(sceneId);}};
        Map<Long, List<Long>> fatherSceneMap = sceneStepDomain.scenesInOther(sceneIds);
        if (!fatherSceneMap.get(sceneId).isEmpty()) {
            throw new AutoTestException("存在子场景循环引用");
        }
    }


}
