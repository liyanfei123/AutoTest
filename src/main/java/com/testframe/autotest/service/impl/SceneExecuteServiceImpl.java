package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.enums.StepStatusEnum;
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

    @Override
    public void execute(Long sceneId) {
        try {
            SceneDetailInfo sceneDetailInfo = sceneDetail.query(sceneId);
            if (sceneDetailInfo.getSteps() == null) {
                throw new AutoTestException("当前场景无可执行的步骤");
            }
            // 过滤掉执行状态关闭的步骤
            List<StepInfoDto> steps = sceneDetailInfo.getSteps();
            steps = steps.stream().filter(step -> step.getStepStatus() == StepStatusEnum.OPEN.getType())
                    .collect(Collectors.toList());
            if (steps.isEmpty()) {
                throw new AutoTestException("当前场景下无开启的步骤");
            }
            Long recordId = sceneRecordService.saveRecord(sceneId);
            SeleniumRunEvent seleniumRunEvent = new SeleniumRunEvent();
            buildEvent(seleniumRunEvent, sceneDetailInfo.getScene(), steps, recordId);
            eventBus.post(seleniumRunEvent);
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:execute] execute scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    private void buildEvent(SeleniumRunEvent seleniumRunEvent, SceneInfoDto sceneInfoDto,
                            List<StepInfoDto> steps, Long recordId) {

        SceneRunInfo sceneRunInfo = SceneRunInfo.build(sceneInfoDto);
        seleniumRunEvent.setSceneRunInfo(sceneRunInfo);
        SceneRunRecordInfo sceneRunRecordInfo = new SceneRunRecordInfo();
        sceneRunRecordInfo.setRecordId(recordId);
        seleniumRunEvent.setSceneRunRecordInfo(sceneRunRecordInfo);
        WaitInfo waitInfo = new WaitInfo(sceneInfoDto.getWaitType(), sceneInfoDto.getWaitTime());
        seleniumRunEvent.setWaitInfo(waitInfo);
        // 转换为StepExeInfo
        List<StepExeInfo> stepExeInfos = new ArrayList<>(steps.size());
        for (StepInfoDto stepInfoDto : steps) {
            StepUIInfo stepUIInfo = stepInfoDto.getStepUIInfo();
            StepExeInfo stepExeInfo = new StepExeInfo();
            stepExeInfo.setStepId(stepInfoDto.getStepId());
            stepExeInfo.setStepName(stepInfoDto.getStepName());
            // 根据操作类型来进行不同的包装
            if (stepInfoDto.getStepUIInfo().getOperateType() == OperateTypeEnum.ASSERT.getType()) {
                AssertData checkData = AssertData.build(stepUIInfo);
                stepExeInfo.setCheckData(checkData);
                LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneInfoDto);
                stepExeInfo.setLocatorInfo(locatorInfo);
                stepExeInfo.setOperateData(null);
                stepExeInfo.setWaitInfo(null);
            } else if (stepInfoDto.getStepUIInfo().getOperateType() == OperateTypeEnum.WAIT.getType()) {
                LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneInfoDto);
                stepExeInfo.setLocatorInfo(locatorInfo);
                stepExeInfo.setCheckData(null);

            } else if (stepInfoDto.getStepUIInfo().getOperateType() == OperateTypeEnum.OPERATE.getType()) {
                LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneInfoDto);
                stepExeInfo.setLocatorInfo(locatorInfo);
                OperateData operateData = OperateData.build(stepUIInfo);
                stepExeInfo.setOperateData(operateData);
                stepExeInfo.setCheckData(null);
                stepExeInfo.setWaitInfo(null);
            }
            stepExeInfos.add(stepExeInfo);
        }
        seleniumRunEvent.setStepExeInfos(stepExeInfos);
    }
}
