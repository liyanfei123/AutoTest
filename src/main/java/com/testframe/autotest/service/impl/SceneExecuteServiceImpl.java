package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.dto.SceneDetailInfo;
import com.testframe.autotest.meta.dto.SceneInfoDto;
import com.testframe.autotest.meta.dto.StepInfoDto;
import com.testframe.autotest.meta.dto.StepUIInfo;
import com.testframe.autotest.service.SceneRecordService;
import com.testframe.autotest.ui.meta.AssertData;
import com.testframe.autotest.ui.enums.OperateTypeEnum;
import com.testframe.autotest.ui.meta.*;
import org.greenrobot.eventbus.EventBus;
import com.testframe.autotest.service.SceneExecuteService;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/11/08 21:59
 * @author: lyf
 */
@Slf4j
@Service
@DependsOn("myEventBus")
public class SceneExecuteServiceImpl implements SceneExecuteService {

    @Autowired
    @Qualifier("myEventBus")
    private EventBus eventBus;

    @Autowired
    private SceneDetailImpl sceneDetail;

    @Autowired
    private SceneRecordService sceneRecordService;

//    public void execute() {
//        log.info("开始发送消息");
//        eventBus.post(SeleniumRunEvent.builder().sceneId(123L).build());
//    }

    public void execute(Long sceneId) {
        try {
            SeleniumRunEvent seleniumRunEvent = generateEvent(sceneId, SceneExecuteEnum.SINGLE.getType());
            eventBus.post(seleniumRunEvent);
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:execute] execute scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    /**
     * 生成执行事件内容
     * @param sceneId
     * @param type SceneExecuteEnum，若是作为子场景执行，传2
     * @return
     */
    public SeleniumRunEvent generateEvent(Long sceneId, Integer type) {
        try {
            SceneDetailInfo sceneDetailInfo = sceneDetail.query(sceneId);
            if (sceneDetailInfo.getSteps() == null) {
                throw new AutoTestException("当前场景无可执行的步骤");
            }
            List<StepInfoDto> steps = sceneDetailInfo.getSteps();
            List<Long> stepOrderList = queryOpenStepOrderList(steps);
//            List<Long> stepSceneIds = steps.stream().filter(stepInfoDto ->
//                    stepInfoDto.getSceneId() != 0 || stepInfoDto.getSceneId() != null).map(StepInfoDto::getSceneId)
//                    .collect(Collectors.toList()); // 当前场景下涉及的子场景id
            Long recordId = sceneRecordService.saveRecord(sceneId, stepOrderList,
                        SceneStatusEnum.ING.getType(), type);
            log.info("[SceneExecuteServiceImpl:execute] generate scene {} execute record Id {}", sceneId, recordId);
            SeleniumRunEvent seleniumRunEvent = buildRunEvent(sceneDetailInfo.getScene(), steps, recordId);
            return seleniumRunEvent;
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:execute] execute scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    private SeleniumRunEvent buildRunEvent(SceneInfoDto sceneInfoDto, List<StepInfoDto> steps, Long recordId) {
        SeleniumRunEvent seleniumRunEvent = new SeleniumRunEvent();
        List<Long> runOrderList = steps.stream().map(StepInfoDto::getStepId).collect(Collectors.toList());
        SceneRunInfo sceneRunInfo = SceneRunInfo.build(sceneInfoDto, runOrderList);
        seleniumRunEvent.setSceneRunInfo(sceneRunInfo);
        SceneRunRecordInfo sceneRunRecordInfo = new SceneRunRecordInfo();
        sceneRunRecordInfo.setRecordId(recordId);
        seleniumRunEvent.setSceneRunRecordInfo(sceneRunRecordInfo);
        WaitInfo waitInfo = new WaitInfo(sceneInfoDto.getWaitType(), sceneInfoDto.getWaitTime());
        seleniumRunEvent.setWaitInfo(waitInfo);
        // 转换为stepExe
        List<StepExe> stepExes = new ArrayList<>(steps.size());
        for (StepInfoDto stepInfoDto : steps) {
            StepUIInfo stepUIInfo = stepInfoDto.getStepUIInfo();
            StepExe stepExe = new StepExe();
            if (stepInfoDto.getType() == StepTypeEnum.SCENE.getType()) { // 子场景
                stepExe.setStepId(stepInfoDto.getStepId());
                stepExe.setStepName(stepInfoDto.getStepName());
                stepExe.setStatus(stepInfoDto.getStepStatus());
                stepExe.setOperaType(null);
                stepExe.setStepSceneId(stepInfoDto.getSonSceneId());
            } else if (stepInfoDto.getType() == StepTypeEnum.STEP.getType()) { // 单步骤
                stepExe.setStepId(stepInfoDto.getStepId());
                stepExe.setStepName(stepInfoDto.getStepName());
                stepExe.setStatus(stepInfoDto.getStepStatus());
                stepExe.setOperaType(stepInfoDto.getStepUIInfo().getOperateType());
                buildStepExe(sceneInfoDto, stepUIInfo, stepInfoDto, stepExe); // 单步骤再单独包装一下
            }
            stepExes.add(stepExe);
        }
        seleniumRunEvent.setStepExes(stepExes);
        return seleniumRunEvent;
    }

    // 查询可运行的步骤编排顺序
    private List<Long> queryOpenStepOrderList(List<StepInfoDto> steps) {
        // 过滤掉执行状态关闭的步骤
        List<StepInfoDto> openSteps = steps.stream().filter(step -> step.getStepStatus() == StepStatusEnum.OPEN.getType())
                .collect(Collectors.toList());
        if (openSteps.isEmpty()) {
            throw new AutoTestException("当前场景下无开启的步骤");
        }
        List<Long> stepOrderList = steps.stream().map(StepInfoDto::getStepId).collect(Collectors.toList());
        return stepOrderList;
    }

    // 将步骤信息修改为事件执行字段信息
    private void buildStepExe(SceneInfoDto sceneInfoDto, StepUIInfo stepUIInfo,
                              StepInfoDto stepInfoDto, StepExe stepExe) {
        // 根据操作类型来进行不同的包装
        if (stepInfoDto.getStepUIInfo().getOperateType() == OperateTypeEnum.ASSERT.getType()) {
            AssertData checkData = AssertData.build(stepUIInfo);
            stepExe.setCheckData(checkData);
            LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneInfoDto);
            stepExe.setLocatorInfo(locatorInfo);
            stepExe.setOperateData(null);
            stepExe.setWaitInfo(null);
        } else if (stepInfoDto.getStepUIInfo().getOperateType() == OperateTypeEnum.WAIT.getType()) {
            LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneInfoDto);
            stepExe.setLocatorInfo(locatorInfo);
            stepExe.setCheckData(null);
            WaitInfo stepWaitInfo;
            if (stepUIInfo.getValue() == null) {
                stepWaitInfo = new WaitInfo(stepUIInfo.getWaitMode(), 0);
            } else {
                stepWaitInfo = new WaitInfo(stepUIInfo.getWaitMode(), Integer.valueOf(stepUIInfo.getValue()));
            }
            stepExe.setWaitInfo(stepWaitInfo);
        } else if (stepInfoDto.getStepUIInfo().getOperateType() == OperateTypeEnum.OPERATE.getType()) {
            LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneInfoDto);
            stepExe.setLocatorInfo(locatorInfo);
            OperateData operateData = OperateData.build(stepUIInfo);
            stepExe.setOperateData(operateData);
            stepExe.setCheckData(null);
            stepExe.setWaitInfo(null);
        }
    }
}
