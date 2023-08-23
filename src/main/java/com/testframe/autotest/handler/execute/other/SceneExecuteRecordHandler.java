package com.testframe.autotest.handler.execute.other;

import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.meta.channel.SeleniumRunEventChannel;
import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.handler.base.HandlerI;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.ui.meta.SetRunRecordInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 场景执行记录处理器
 */
@Slf4j
@Component
public class SceneExecuteRecordHandler implements HandlerI<SeleniumRunEventChannel> {

    @Autowired
    private RecordDomain recordDomain;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private SceneExecuteRecordConverter sceneExecuteRecordConverter;
    @Override
    public Boolean handle(SeleniumRunEventChannel channel) {
        try {
            SceneDetailDto sceneDetailDto = channel.getSceneDetailDto();
            if (sceneDetailDto == null) {
                log.error("[SceneExecuteRecordHandler:generateSceneExecuteRecord] 参数错误");
                throw new AutoTestException("参数错误");
            }
            int type = channel.getType();
            Long setRecordId = 0L;
            switch (SceneExecuteEnum.getByType(type)) {
                case SINGLE:
                case BELOW:
                    channel.setSceneSetConfigModel(null);
                    channel.setSetRunRecordInfo(null);
                    break;
                case SET:
                    // 获取执行集执行记录信息
                    SetRunRecordInfo setRunRecordInfo = channel.getSetRunRecordInfo();
                    if (setRunRecordInfo == null || setRunRecordInfo.getSetRecordId() == 0) {
                        setRecordId = 0L;
                    } else {
                        setRecordId = setRunRecordInfo.getSetRecordId();
                    }
                    break;
                default:
                    return false;
            }
            this.generateSceneExecuteRecord(channel, sceneDetailDto, setRecordId);
        } catch (Exception e) {
            log.error("[SceneExecuteRecordHandler:handle] handle scene execute record error, reason = {}", e);
            return false;
        }
        return true;
    }


    private void generateSceneExecuteRecord(SeleniumRunEventChannel channel, SceneDetailDto sceneDetailDto, Long setRecordId) {
        Long sceneId = sceneDetailDto.getSceneId();
        // 生成执行记录
        StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
        List<Long> stepOrderList = stepOrderDo.getOrderList();
        SceneExecuteRecordDto sceneExecuteRecordDto = sceneExecuteRecordConverter.buildSceneExecuteRecord(sceneDetailDto);
        sceneExecuteRecordDto.setSetRecordId(setRecordId);
        SceneSetConfigModel sceneSetConfigModel = channel.getSceneSetConfigModel();
        if (sceneSetConfigModel != null) {
            sceneExecuteRecordDto.setWaitTime(sceneSetConfigModel.getTimeOutTime());
        }
        sceneExecuteRecordDto.setStatus(SceneStatusEnum.INT.getType());
        sceneExecuteRecordDto.setType(channel.getType());
        sceneExecuteRecordDto.setStepOrderList(stepOrderList);
        if (channel.getType() == SceneExecuteEnum.BELOW.getType()) {
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
    }


}
