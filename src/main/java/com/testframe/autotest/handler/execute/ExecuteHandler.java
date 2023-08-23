package com.testframe.autotest.handler.execute;

import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.SetRunResultEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.record.SetRecordDomain;
import com.testframe.autotest.handler.base.HandlerI;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class ExecuteHandler implements HandlerI<ExecuteChannel> {

    @Resource
    private RecordDomain recordDomain;

    @Resource
    private SetRecordDomain setRecordDomain;

    @Override
    public Boolean handle(ExecuteChannel channel) {
        try {
            recordSetExecuteRecord(channel);
            recordSceneExecuteRecord(channel);
            buildRunSort(channel);
        } catch (AutoTestException e) {
            // 初始化失败时，将执行集执行状态修改
            initError(channel, e);
            return false;
        }
        return true;
    }


    /**
     * 记录执行集执行记录
     * @param channel
     */
    private void recordSetExecuteRecord(ExecuteChannel channel) {
        SetExecuteRecordDto setExecuteRecordDto = channel.getSetExecuteRecordDto();
        if (setExecuteRecordDto == null) {
            return;
        }
        Long setRecordId = setRecordDomain.updateSetExeRecord(setExecuteRecordDto);
        setExecuteRecordDto.setSetRecordId(setRecordId);
        // 生成执行记录后，需要对场景进行关联
        Collection<SceneExecuteRecordDto> list = channel.getSceneExecuteRecordDtoHashMap().values();
        List<SceneExecuteRecordDto> sceneExecuteRecordDtos = new ArrayList<>(list);
        sceneExecuteRecordDtos.forEach(sceneExecuteRecordDto -> sceneExecuteRecordDto.setSetRecordId(setRecordId));
    }


    /**
     * 记录场景执行记录
     * @param channel
     */
    private void recordSceneExecuteRecord(ExecuteChannel channel) {
        HashMap<Long, SceneExecuteRecordDto> sceneExecuteRecordDtoMap = channel.getSceneExecuteRecordDtoHashMap();
        for (SceneExecuteRecordDto sceneExecuteRecordDto : sceneExecuteRecordDtoMap.values()) {
            Long recordId = recordDomain.updateSceneExeRecord(sceneExecuteRecordDto, null);
            log.info("[ExecuteHandler:recordSceneExecuteRecord] generate scene recordId, recordId = {}", recordId);
            if (recordId == 0) {
                throw new AutoTestException("场景执行记录保存失败");
            }
            sceneExecuteRecordDto.setRecordId(recordId);
        }
    }

    private void buildRunSort(ExecuteChannel channel) {
        List<SceneDetailDto> sceneDetailDtos = channel.getSceneDetailDtos();
        HashMap<Long, List<StepDetailDto>> stepDetailDtoHashMap = channel.getStepDetailDtoHashMap();
        HashMap<Long, SceneExecuteRecordDto> sceneExecuteRecordDtoHashMap = channel.getSceneExecuteRecordDtoHashMap();
        for (SceneDetailDto sceneDetailDto : sceneDetailDtos) {
            try {
                // 编排步骤执行顺序
                List<StepDetailDto> stepDetailDtos = stepDetailDtoHashMap.get(sceneDetailDto.getSceneId());
                List<Long> stepOrderList = sceneExecuteRecordDtoHashMap.get(sceneDetailDto.getSceneId()).getStepOrderList();
                Collections.sort(stepDetailDtos, new Comparator<StepDetailDto>() {
                    @Override
                    public int compare(StepDetailDto step1, StepDetailDto step2) {
                        return stepOrderList.indexOf(step1.getStepId()) - stepOrderList.indexOf(step2.getStepId());
                    }
                });
            } catch (AutoTestException e) {
                // 这边当事件执行失败时，需要将该场景的初始化更新为失败
                SceneExecuteRecordDto sceneExecuteRecordDto = sceneExecuteRecordDtoHashMap.get(sceneDetailDto.getSceneId());
                sceneExecuteRecordDto.setStatus(SceneStatusEnum.INTFAIL.getType());
                recordDomain.updateSceneExeRecord(sceneExecuteRecordDto, null);
                log.error("[ExecuteHandler:buildRunSort] build sort fail, sceneDetailDto = {}, reason = {}",
                        sceneDetailDto, e);
                throw new AutoTestException(e.getMessage());
            }
        }

    }

    private void initError(ExecuteChannel channel, AutoTestException e) {
        SetExecuteRecordDto setExecuteRecordDto = channel.getSetExecuteRecordDto();
        if (setExecuteRecordDto == null) {
            log.error("[ExecuteHandler:initError] update set record error,  reason = ", e);
        }
        // 更新执行集合记录
        if (setExecuteRecordDto.getSetRecordId() != null) {
            setExecuteRecordDto.setStatus(SetRunResultEnum.FAIL.getType());
            Long result = setRecordDomain.updateSetExeRecord(setExecuteRecordDto);
            if (result == 0L) {
                log.error("[ExecuteHandler:initError] update set record error, setExecuteRecordDto = {},  reason = {}",
                        setExecuteRecordDto, e);
            }
        }
        throw new AutoTestException(e.getMessage());
    }

}
