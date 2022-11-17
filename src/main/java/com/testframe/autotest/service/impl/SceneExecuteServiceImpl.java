package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.dto.SceneDetailInfo;
import com.testframe.autotest.meta.dto.SceneInfoDto;
import com.testframe.autotest.meta.dto.StepInfoDto;
import com.testframe.autotest.service.SceneRecordService;
import com.testframe.autotest.ui.meta.LocatorInfo;
import com.testframe.autotest.ui.meta.OperateData;
import com.testframe.autotest.ui.meta.StepExeInfo;
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

    public void execute() {
        log.info("开始发送消息");
        eventBus.post(SeleniumRunEvent.builder().sceneId(123L).build());
    }

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
            seleniumRunEvent.setSceneId(sceneId);
            seleniumRunEvent.setRecordId(recordId);

        } catch (Exception e) {
            throw new AutoTestException("场景执行失败");
        }
    }

    private void buildEvent(SeleniumRunEvent seleniumRunEvent, SceneInfoDto sceneInfoDto, List<StepInfoDto> steps) {
        // 转换为StepExeInfo
        List<StepExeInfo> stepExeInfos = new ArrayList<>(steps.size());
        for (StepInfoDto stepInfoDto : steps) {
            StepExeInfo stepExeInfo = new StepExeInfo();
            stepExeInfo.setStepId(stepInfoDto.getStepId());
            stepExeInfo.setStepName(stepInfoDto.getStepName());
            LocatorInfo locatorInfo = LocatorInfo.build(stepInfoDto.getStepUIInfo(), sceneInfoDto);
            stepExeInfo.setLocatorInfo(locatorInfo);
            // 根据操作类型来进行不同的包装
            // 检验类型需要给checkData赋值
            OperateData operateData;
        }

    }
}
