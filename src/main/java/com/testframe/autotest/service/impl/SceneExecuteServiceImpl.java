package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.domain.step.StepDomain;
import com.testframe.autotest.ui.meta.StepUIInfo;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.model.StepInfoModel;
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

import java.util.*;
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
    private SceneDomain sceneDomain;

    @Autowired
    private StepDomain stepDomain;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private RecordDomain recordDomain;

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
            SceneDetailDto sceneDetailDto = sceneDomain.getSceneById(sceneId);
            if (sceneDetailDto == null) {
                throw new AutoTestException("请输入正确的场景id");
            }
            List<StepDetailDto> stepDetailDtos = stepDomain.listStepInfo(sceneId);

            // 过滤掉执行状态关闭的步骤
            List<StepDetailDto> openSteps = stepDetailDtos.stream().filter(stepDetailDto -> stepDetailDto.getStepStatus() == StepStatusEnum.OPEN.getType())
                    .collect(Collectors.toList());
            if (openSteps == null || openSteps.isEmpty()) {
                throw new AutoTestException("当前场景无可执行的步骤");
            }

            // 生成执行记录
            StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            List<Long> stepOrderList = stepOrderDo.getOrderList();
//            List<Long> stepSceneIds = steps.stream().filter(stepInfoDto ->
//                    stepInfoDto.getSceneId() != 0 || stepInfoDto.getSceneId() != null).map(StepInfoDto::getSceneId)
//                    .collect(Collectors.toList()); // 当前场景下涉及的子场景id
            SceneExecuteRecordDto sceneExecuteRecordDto = this.buildInit(sceneDetailDto);
            sceneExecuteRecordDto.setStatus(SceneStatusEnum.INT.getType());
            sceneExecuteRecordDto.setType(type);
            sceneExecuteRecordDto.setStepOrderList(stepOrderList);
            if (type == StepTypeEnum.SCENE.getType()) {
                // 子场景执行不需要读取这些配置
                sceneExecuteRecordDto.setUrl(null);
                sceneExecuteRecordDto.setWaitType(null);
                sceneExecuteRecordDto.setWaitTime(null);
            }

            Long recordId = recordDomain.updateSceneExeRecord(sceneExecuteRecordDto, null);
            if (recordId == 0) {
                throw new AutoTestException("场景执行失败");
            }

            log.info("[SceneExecuteServiceImpl:execute] generate scene {} execute record Id {}", sceneId, recordId);
            try {
                // 编排步骤执行顺序
                Collections.sort(stepDetailDtos, new Comparator<StepDetailDto>() {
                    @Override
                    public int compare(StepDetailDto step1, StepDetailDto step2) {
                        return stepOrderList.indexOf(step1.getStepId()) - stepOrderList.indexOf(step2.getStepId());
                    }
                });
                // 生成执行事件
                SeleniumRunEvent seleniumRunEvent = buildRunEvent(sceneDetailDto, stepDetailDtos, recordId, stepOrderList);
                return seleniumRunEvent;
            } catch (AutoTestException e) {
                // 这边当事件执行失败时，需要将该场景的初始化更新为失败
                sceneExecuteRecordDto.setRecordId(recordId);
                sceneExecuteRecordDto.setStatus(SceneStatusEnum.INTFAIL.getType());
                recordDomain.updateSceneExeRecord(sceneExecuteRecordDto, null);
                log.error("[SceneExecuteServiceImpl:execute] init event fail, scene {} error, reason = {}", sceneId, e);
                throw new AutoTestException(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:execute] execute scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    private SceneExecuteRecordDto buildInit(SceneDetailDto sceneDetailDto) {
        SceneExecuteRecordDto sceneExecuteRecordDto = new SceneExecuteRecordDto();
        sceneExecuteRecordDto.setRecordId(null);
        sceneExecuteRecordDto.setSceneId(sceneDetailDto.getSceneId());
        sceneExecuteRecordDto.setSceneName(sceneDetailDto.getSceneName());
        sceneExecuteRecordDto.setUrl(sceneDetailDto.getUrl());
        sceneExecuteRecordDto.setWaitType(sceneDetailDto.getWaitType());
        sceneExecuteRecordDto.setWaitTime(sceneDetailDto.getWaitTime());
        sceneExecuteRecordDto.setStatus(null);
        sceneExecuteRecordDto.setType(null);
        Long currentTime = System.currentTimeMillis();
        sceneExecuteRecordDto.setExecuteTime(currentTime);
        sceneExecuteRecordDto.setExtInfo(null);
        sceneExecuteRecordDto.setStepOrderList(null);
        return sceneExecuteRecordDto;
    }

    private SeleniumRunEvent buildRunEvent(SceneDetailDto sceneDetailDto, List<StepDetailDto> stepDetailDtos,
                                           Long recordId, List<Long> stepOrderList) {
        SeleniumRunEvent seleniumRunEvent = new SeleniumRunEvent();
        try {
            SceneRunInfo sceneRunInfo = SceneRunInfo.build(sceneDetailDto, stepOrderList);
            seleniumRunEvent.setSceneRunInfo(sceneRunInfo);
            SceneRunRecordInfo sceneRunRecordInfo = new SceneRunRecordInfo();
            sceneRunRecordInfo.setRecordId(recordId);
            seleniumRunEvent.setSceneRunRecordInfo(sceneRunRecordInfo);
            WaitInfo waitInfo = new WaitInfo(sceneDetailDto.getWaitType(), sceneDetailDto.getWaitTime());
            seleniumRunEvent.setWaitInfo(waitInfo);
            // 转换为stepExe
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
            return seleniumRunEvent;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException("事件初始化过程失败");
        }
    }

    // 将步骤信息修改为事件执行字段信息
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
