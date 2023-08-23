package com.testframe.autotest.handler.execute;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.handler.base.HandlerI;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.meta.model.StepInfoModel;
import com.testframe.autotest.ui.enums.OperateTypeEnum;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
import com.testframe.autotest.ui.meta.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SeleniumRunEventHandler implements HandlerI<ExecuteChannel> {

    @Override
    public Boolean handle(ExecuteChannel channel) {
        List<SeleniumRunEvent> seleniumRunEvents = new ArrayList<>();
        List<SceneDetailDto> sceneDetailDtos = channel.getSceneDetailDtos();
        HashMap<Long, SceneExecuteRecordDto> sceneExecuteRecordDtoHashMap = channel.getSceneExecuteRecordDtoHashMap();
        HashMap<Long, SceneSetConfigModel> sceneSetConfigModelHashMap = channel.getSceneSetConfigModelHashMap();
        HashMap<Long, List<StepDetailDto>> stepDetailDtoHashMap = channel.getStepDetailDtoHashMap();
        for (SceneDetailDto sceneDetailDto : sceneDetailDtos) {
            Long sceneId = sceneDetailDto.getSceneId();
            SeleniumRunEvent seleniumRunEvent = new SeleniumRunEvent();
            seleniumRunEvent.setBrowserType(channel.getBrowserType());
            seleniumRunEvent.setType(channel.getType());
            this.buildSetRunRecordInfo(channel, seleniumRunEvent);
            this.buildSceneRunRecordInfo(channel, seleniumRunEvent, sceneExecuteRecordDtoHashMap.get(sceneId));
            this.buildSceneRunInfo(channel, seleniumRunEvent, sceneDetailDto, sceneExecuteRecordDtoHashMap.get(sceneId));
            this.buildWaitInfo(channel, seleniumRunEvent, sceneDetailDto,
                    sceneSetConfigModelHashMap.getOrDefault(sceneId, null));
            this.buildStepExes(channel, seleniumRunEvent, sceneDetailDto, stepDetailDtoHashMap.get(sceneId));
            seleniumRunEvents.add(seleniumRunEvent);
        }
        int browserType = channel.getExecuteCmd().getBrowserType();
        seleniumRunEvents.forEach(seleniumRunEvent -> seleniumRunEvent.setBrowserType(browserType));
        channel.setSeleniumRunEvents(seleniumRunEvents);
        return true;
    }

    private void buildType(ExecuteChannel channel, SeleniumRunEvent seleniumRunEvent) {
        SetExecuteRecordDto setExecuteRecordDto = channel.getSetExecuteRecordDto();
        if (setExecuteRecordDto == null) {
            seleniumRunEvent.setType(SceneExecuteEnum.SINGLE.getType());
        } else {
            seleniumRunEvent.setType(SceneExecuteEnum.SET.getType());
        }
    }

    private void buildSetRunRecordInfo(ExecuteChannel channel, SeleniumRunEvent seleniumRunEvent) {
        SetExecuteRecordDto setExecuteRecordDto = channel.getSetExecuteRecordDto();
        if (setExecuteRecordDto == null) {
            return;
        }
        SetRunRecordInfo setRunRecordInfo = new SetRunRecordInfo();
        setRunRecordInfo.setSetRecordId(setExecuteRecordDto.getSetRecordId());
        setRunRecordInfo.setSetId(setExecuteRecordDto.getSetId());
        seleniumRunEvent.setSetRunRecordInfo(setRunRecordInfo);
    }

    private void buildSceneRunRecordInfo(ExecuteChannel channel, SeleniumRunEvent seleniumRunEvent,
                                         SceneExecuteRecordDto sceneExecuteRecordDto) {
        SceneRunRecordInfo sceneRunRecordInfo = new SceneRunRecordInfo();
        sceneRunRecordInfo.setRecordId(sceneExecuteRecordDto.getRecordId());
        seleniumRunEvent.setSceneRunRecordInfo(sceneRunRecordInfo);
    }

    private void buildSceneRunInfo(ExecuteChannel channel, SeleniumRunEvent seleniumRunEvent,
                                   SceneDetailDto sceneDetailDto, SceneExecuteRecordDto sceneExecuteRecordDto) {
        SceneRunInfo sceneRunInfo = SceneRunInfo.build(sceneDetailDto, sceneExecuteRecordDto.getStepOrderList());
        seleniumRunEvent.setSceneRunInfo(sceneRunInfo);
    }

    private void buildStepExes(ExecuteChannel channel, SeleniumRunEvent seleniumRunEvent,
                               SceneDetailDto sceneDetailDto, List<StepDetailDto> stepDetailDtos) {
        List<StepExe> stepExes = new ArrayList<>(stepDetailDtos.size());
        for (StepDetailDto stepDetailDto : stepDetailDtos) {
            StepInfoModel stepInfoModel = JSON.parseObject(stepDetailDto.getStepUIInfo(), StepInfoModel.class);
            StepExe stepExe = new StepExe();
            if (stepDetailDto.getType() == StepTypeEnum.SCENE.getType()) { // 子场景
                stepExe.setStepId(stepDetailDto.getStepId());
                stepExe.setStepName(stepDetailDto.getStepName());
                stepExe.setStatus(stepDetailDto.getStepStatus());
                stepExe.setOperaType(null);
                stepExe.setStepSceneId(stepDetailDto.getSonSceneId());
            } else if (stepDetailDto.getType() == StepTypeEnum.STEP.getType()) { // 单步骤
                stepExe.setStepId(stepDetailDto.getStepId());
                stepExe.setStepName(stepDetailDto.getStepName());
                stepExe.setStatus(stepDetailDto.getStepStatus());
                // 具体的步骤执行策略
                StepUIInfo stepUIInfo = StepUIInfo.build(stepInfoModel);
                stepExe.setOperaType(stepUIInfo.getOperateType());
                buildStepExe(sceneDetailDto, stepUIInfo, stepExe); // 单步骤再单独包装一下
            }
            stepExes.add(stepExe);
        }
        seleniumRunEvent.setStepExes(stepExes);
    }

    private void buildWaitInfo(ExecuteChannel channel, SeleniumRunEvent seleniumRunEvent,
                               SceneDetailDto sceneDetailDto, SceneSetConfigModel sceneSetConfigModel) {
        WaitInfo waitInfo = new WaitInfo(sceneDetailDto.getWaitType(), sceneDetailDto.getWaitTime());
        // 执行集中关联的场景配置
        if (sceneSetConfigModel != null) {
            waitInfo.setWaitTime(sceneSetConfigModel.getTimeOutTime());
        }
        seleniumRunEvent.setWaitInfo(waitInfo);
    }


    private void buildStepExe(SceneDetailDto sceneDetailDto, StepUIInfo stepUIInfo,
                              StepExe stepExe) {
        // 根据操作类型来进行不同的包装
        if (stepUIInfo.getOperateType() == OperateTypeEnum.ASSERT.getType()) {
            AssertData checkData = AssertData.build(stepUIInfo);
            stepExe.setCheckData(checkData);
            LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneDetailDto);
            stepExe.setLocatorInfo(locatorInfo);
            stepExe.setOperateData(null);
            stepExe.setWaitInfo(null);
        } else if (stepUIInfo.getOperateType() == OperateTypeEnum.WAIT.getType()) {
            LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneDetailDto);
            stepExe.setLocatorInfo(locatorInfo);
            stepExe.setCheckData(null);
            WaitInfo stepWaitInfo;
            if (stepUIInfo.getValue() == null) {
                stepWaitInfo = new WaitInfo(stepUIInfo.getWaitMode(), 0);
            } else {
                stepWaitInfo = new WaitInfo(stepUIInfo.getWaitMode(), Integer.valueOf(stepUIInfo.getValue()));
            }
            stepExe.setWaitInfo(stepWaitInfo);
        } else if (stepUIInfo.getOperateType() == OperateTypeEnum.OPERATE.getType()) {
            LocatorInfo locatorInfo = LocatorInfo.build(stepUIInfo, sceneDetailDto);
            stepExe.setLocatorInfo(locatorInfo);
            OperateData operateData = OperateData.build(stepUIInfo);
            stepExe.setOperateData(operateData);
            stepExe.setCheckData(null);
            stepExe.setWaitInfo(null);
        }
    }
}
