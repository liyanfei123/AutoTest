package com.testframe.autotest.handler.execute.other;

import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.SetRunResultEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.meta.channel.SeleniumRunEventChannel;
import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.record.SetRecordDomain;
import com.testframe.autotest.handler.base.HandlerI;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
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
public class SetExecuteRecordHandler implements HandlerI<SeleniumRunEventChannel> {

    @Autowired
    private SetRecordDomain setRecordDomain;

    @Override
    public Boolean handle(SeleniumRunEventChannel channel) {
        try {
            int type = channel.getType();
            switch (SceneExecuteEnum.getByType(type)) {
                case SINGLE:
                case BELOW:
                    channel.setSetRunRecordInfo(null);
                    break;
                case SET:
                    SetRunRecordInfo setRunRecordInfo = generateSetExecuteRecord(channel);
                    channel.setSetRunRecordInfo(setRunRecordInfo);
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("[SetExecuteRecordHandler:handle] handle set execute record error, reason = {}", e);
            return false;
        }
        return true;
    }


    private SetRunRecordInfo generateSetExecuteRecord(SeleniumRunEventChannel channel) {
        ExeSetDto exeSetDto = channel.getExeSetDto();
        if (exeSetDto == null) {
            log.error("[SetExecuteRecordHandler:generateSetExecuteRecord] error");
            throw new AutoTestException("参数错误");
        }
        // 生成执行集执行记录id
        SetExecuteRecordDto setExecuteRecordDto = new SetExecuteRecordDto();
        setExecuteRecordDto.setSetId(exeSetDto.getSetId());
        setExecuteRecordDto.setSetName(exeSetDto.getSetName());
        setExecuteRecordDto.setStatus(SetRunResultEnum.NORUN.getType());
        Long setRecordId = setRecordDomain.updateSetExeRecord(setExecuteRecordDto);
        SetRunRecordInfo setRunRecordInfo = new SetRunRecordInfo();
        setRunRecordInfo.setSetRecordId(setRecordId);
        setRunRecordInfo.setSetId(exeSetDto.getSetId());
        return setRunRecordInfo;
    }

}
