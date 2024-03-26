package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.core.enums.*;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.Response;
import com.testframe.autotest.core.repository.ExeSetRepository;
import com.testframe.autotest.core.repository.SetExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.record.SetRecordDomain;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.domain.sceneSet.SceneSetDomain;
import com.testframe.autotest.domain.step.StepDomain;
import com.testframe.autotest.handler.HandlerHolder;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.command.ExecuteCmd;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.meta.query.RecordQry;
import com.testframe.autotest.meta.validation.execute.ExecuteValidation;
import com.testframe.autotest.ui.enums.BrowserEnum;
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
import org.openqa.selenium.json.Json;
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
    private SceneSetDomain sceneSetDomain;

    @Autowired
    private SetRecordDomain setRecordDomain;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Autowired
    private RecordDomain recordDomain;

    @Autowired
    private SceneCacheService sceneCacheService;

    @Autowired
    private HandlerHolder handlerHolder;

    @Autowired
    private ExecuteValidation executeValidation;

//    public void execute() {
//        log.info("开始发送消息");
//        eventBus.post(SeleniumRunEvent.builder().sceneId(123L).build());
//    }

    public void executeScene(Long sceneId, Integer browserType) {
        try {
            if (BrowserEnum.getByType(browserType) == null) {
                throw new AutoTestException("浏览器选择错误");
            }
            SeleniumRunEvent seleniumRunEvent = generateEvent(0L, sceneId, null, SceneExecuteEnum.SINGLE.getType());
            seleniumRunEvent.setBrowserType(browserType);
            log.info("[SceneExecuteServiceImpl:executeScene] post event, event = {}", JSON.toJSONString(seleniumRunEvent));
            eventBus.post(seleniumRunEvent);
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:executeScene] execute scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public void executeSet(Long setId, Integer browserType) {
        if (BrowserEnum.getByType(browserType) == null) {
            throw new AutoTestException("浏览器选择错误");
        }
        // 检验执行集是否存在
        ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(setId);
        if (exeSetDo == null) {
            throw new AutoTestException("执行集id错误");
        }

        // 目前只支持场景执行
        PageQry pageQry = new PageQry();
        pageQry.setSize(-1); // 查找所有
        SceneSetBo sceneSetBo = sceneSetDomain.querySetBySetId(setId, SetMemTypeEnum.SCENE.getType(),
                OpenStatusEnum.OPEN.getType(), pageQry);
        List<SceneSetRelSceneBo> sceneSetRelSceneBos = sceneSetBo.getSceneSetRelSceneBos();
        if (sceneSetRelSceneBos.isEmpty()) {
            throw new AutoTestException("当前执行集下无可执行场景");
        }
        List<Long> sceneIds = sceneSetRelSceneBos.stream().map(sceneSetRelSceneBo -> sceneSetRelSceneBo.getSceneId())
                .collect(Collectors.toList());

        // 生成执行集执行记录id
        SetExecuteRecordDto setExecuteRecordDto = new SetExecuteRecordDto();
        setExecuteRecordDto.setSetId(setId);
        setExecuteRecordDto.setSetName(exeSetDo.getSetName());
        setExecuteRecordDto.setStatus(SetRunResultEnum.NORUN.getType());
        Long setRecordId = setRecordDomain.updateSetExeRecord(setExecuteRecordDto);
        SetRunRecordInfo setRunRecordInfo = new SetRunRecordInfo();
        setRunRecordInfo.setSetRecordId(setRecordId);
        setRunRecordInfo.setSetId(setId);

        // 执行集场景初始化失败
        List<SeleniumRunEvent> seleniumRunEvents = new ArrayList<>();
        try {
            for (SceneSetRelSceneBo sceneSetRelSceneBo : sceneSetRelSceneBos) {
                Long sceneId = sceneSetRelSceneBo.getSceneId();
                SceneSetConfigModel sceneSetConfigModel = sceneSetRelSceneBo.getSceneSetConfigModel();
                SeleniumRunEvent seleniumRunEvent = generateEvent(setRecordId, sceneId, sceneSetConfigModel, SceneExecuteEnum.SINGLE.getType());
                seleniumRunEvent.setBrowserType(browserType);
                seleniumRunEvent.setSetRunRecordInfo(setRunRecordInfo);
                seleniumRunEvents.add(seleniumRunEvent);
            }
            log.info("[SceneExecuteServiceImpl:executeSet] post event, events = {}", JSON.toJSONString(seleniumRunEvents));
            eventBus.post(seleniumRunEvents);
        } catch (AutoTestException e) {
            // 初始化失败时，将执行集执行状态修改
            log.error("[SceneExecuteServiceImpl:executeSet] execute set {} error, reason = {}", setId, e);
            setExecuteRecordDto.setSetRecordId(setRecordId);
            setExecuteRecordDto.setStatus(SetRunResultEnum.FAIL.getType());
            Long result = setRecordDomain.updateSetExeRecord(setExecuteRecordDto);
            if (result == 0L) {
                log.error("[SceneExecuteServiceImpl:executeSet] update set record {} error, setRecordId = {},  reason = {}",
                        setId, setRecordId, e);
            }
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public void executeV2(ExecuteCmd executeCmd) {
        log.info("[SceneExecuteServiceImpl:executeV2] execute, executeCmd = {}", JSON.toJSONString(executeCmd));
        if (executeCmd.getBrowserType() == null) {
            executeCmd.setBrowserType(BrowserEnum.CHROME.getType());
        }
        if (BrowserEnum.getByType(executeCmd.getBrowserType()) == null) {
            throw new AutoTestException("浏览器选择错误");
        }
        try {
            ExecuteChannel executeChannel = new ExecuteChannel();
            executeChannel.setExecuteCmd(executeCmd);
            if (executeCmd.getSetId() != null && executeCmd.getSetId() > 0) {
                executeChannel.setType(SceneExecuteEnum.SET.getType());
            } else {
                executeChannel.setType(SceneExecuteEnum.SINGLE.getType());
            }
            Response<ExecuteChannel> response = executeValidation.validate(executeChannel);
            handlerHolder.getSeleniumEventChain().process(response.getResult());
            List<SeleniumRunEvent> seleniumRunEvents = executeChannel.getSeleniumRunEvents();
            log.info("[SceneExecuteServiceImpl:executeV2] post event, events = {}", JSON.toJSONString(seleniumRunEvents));
            eventBus.post(seleniumRunEvents);
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:executeV2] execute error, e = ", e);
            throw new AutoTestException(e.getMessage());
        }

    }


    /**
     * 生成执行事件内容
     * @param setRecordId 执行集记录id
     * @param sceneId
     * @param type SceneExecuteEnum，若是作为子场景执行，传2
     * @return
     */
    @Override
    public SeleniumRunEvent generateEvent(Long setRecordId, Long sceneId, SceneSetConfigModel sceneSetConfigModel, Integer type) {
        try {
            SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(sceneId);
            if (sceneDetailDto == null) {
                throw new AutoTestException("请输入正确的场景id");
            }
//            // 判断当前是否有正常进行中的场景
//            List sceneIds = new ArrayList(){{add(sceneId);}};
//            HashMap<Long, SceneSimpleExecuteDto> recordMap = recordDomain.listRecSceneSimpleExeRecord(sceneIds);
//            SceneSimpleExecuteDto sceneSimpleExecuteDto = recordMap.get(sceneId);
//            if (sceneSimpleExecuteDto != null && sceneSimpleExecuteDto.getStatus() == SceneStatusEnum.ING.getType()) {
//                throw new AutoTestException("当前场景正在执行中，勿重复执行");
//            }

            List<StepDetailDto> stepDetailDtos = stepDomain.listStepInfo(sceneId);
            // 过滤掉执行状态关闭的步骤
            List<StepDetailDto> openSteps = stepDetailDtos.stream().filter(stepDetailDto ->
                            stepDetailDto.getStepStatus() == OpenStatusEnum.OPEN.getType())
                    .collect(Collectors.toList());
            if (openSteps == null || openSteps.isEmpty()) {
                throw new AutoTestException("当前场景无可执行的步骤");
            }

            // 生成执行记录
            StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            List<Long> stepOrderList = stepOrderDo.getOrderList();
            SceneExecuteRecordDto sceneExecuteRecordDto = this.buildInit(setRecordId, sceneDetailDto);
            if (sceneSetConfigModel != null) {
                sceneExecuteRecordDto.setWaitTime(sceneSetConfigModel.getTimeOutTime());
            }
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
            log.info("[SceneExecuteServiceImpl:generateEvent] generate scene recordId, recordId = {}", recordId);
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
                SeleniumRunEvent seleniumRunEvent = buildRunEvent(sceneDetailDto, stepDetailDtos,
                        sceneSetConfigModel, recordId, stepOrderList);
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

    private SceneExecuteRecordDto buildInit(Long setRecordId, SceneDetailDto sceneDetailDto) {
        SceneExecuteRecordDto sceneExecuteRecordDto = new SceneExecuteRecordDto();
        sceneExecuteRecordDto.setRecordId(null);
        sceneExecuteRecordDto.setSetRecordId(setRecordId);
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
                                           SceneSetConfigModel sceneSetConfigModel, Long recordId, List<Long> stepOrderList) {
        SeleniumRunEvent seleniumRunEvent = new SeleniumRunEvent();
        log.info("[SceneExecuteServiceImpl:buildRunEvent] start build run event, scene = {}",
                JSON.toJSONString(sceneDetailDto));
        try {
            SceneRunInfo sceneRunInfo = SceneRunInfo.build(sceneDetailDto, stepOrderList);
            seleniumRunEvent.setSceneRunInfo(sceneRunInfo);
            SceneRunRecordInfo sceneRunRecordInfo = new SceneRunRecordInfo();
            sceneRunRecordInfo.setRecordId(recordId);
            seleniumRunEvent.setSceneRunRecordInfo(sceneRunRecordInfo);
            WaitInfo waitInfo = new WaitInfo(sceneDetailDto.getWaitType(), sceneDetailDto.getWaitTime());
            // 执行集中关联的场景配置
            if (sceneSetConfigModel != null) {
                waitInfo.setWaitTime(sceneSetConfigModel.getTimeOutTime());
            }
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
            log.info("[SceneExecuteServiceImpl:buildRunEvent] build run event end");
            return seleniumRunEvent;
        } catch (Exception e) {
            log.error("[SceneExecuteServiceImpl:buildRunEvent] has error, reason = {}", e);
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
