package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.Do.SetExecuteRecordDo;
import com.testframe.autotest.core.meta.po.SetRecord;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import org.springframework.stereotype.Component;

@Component
public class SetExecuteRecordConvertor implements ConverterI<SetRecord, SetExecuteRecordDo, SetExecuteRecordDto> {

    @Override
    public SetExecuteRecordDo PoToDo(SetRecord setRecord) {
        SetExecuteRecordDo setExecuteRecordDo = new SetExecuteRecordDo();
        setExecuteRecordDo.setSetRecordId(setRecord.getId());
        setExecuteRecordDo.setSetId(setRecord.getSetId());
        setExecuteRecordDo.setSetName(setRecord.getSetName());
        setExecuteRecordDo.setStatus(setRecord.getStatus());
        setExecuteRecordDo.setExecuteTime(setRecord.getCreateTime());
        return setExecuteRecordDo;
    }

    @Override
    public SetRecord DoToPo(SetExecuteRecordDo setExecuteRecordDo) {
        SetRecord setRecord = new SetRecord();
        setRecord.setId(setExecuteRecordDo.getSetRecordId());
        setRecord.setSetId(setExecuteRecordDo.getSetId());
        setRecord.setSetName(setExecuteRecordDo.getSetName());
        setRecord.setStatus(setExecuteRecordDo.getStatus());
        return setRecord;
    }

    @Override
    public SetExecuteRecordDo DtoToDo(SetExecuteRecordDto setExecuteRecordDto) {
        SetExecuteRecordDo setExecuteRecordDo = new SetExecuteRecordDo();
        setExecuteRecordDo.setSetRecordId(setExecuteRecordDto.getSetRecordId());
        setExecuteRecordDo.setSetId(setExecuteRecordDto.getSetId());
        setExecuteRecordDo.setSetName(setExecuteRecordDto.getSetName());
        setExecuteRecordDo.setStatus(setExecuteRecordDto.getStatus());
        return setExecuteRecordDo;
    }

    @Override
    public SetExecuteRecordDto DoToDto(SetExecuteRecordDo setExecuteRecordDo) {
        SetExecuteRecordDto setExecuteRecordDto = new SetExecuteRecordDto();
        setExecuteRecordDto.setSetRecordId(setExecuteRecordDo.getSetRecordId());
        setExecuteRecordDto.setSetId(setExecuteRecordDo.getSetId());
        setExecuteRecordDto.setSetName(setExecuteRecordDo.getSetName());
        setExecuteRecordDto.setStatus(setExecuteRecordDo.getStatus());
        setExecuteRecordDto.setExecuteTime(setExecuteRecordDo.getExecuteTime());
        return null;
    }
}
