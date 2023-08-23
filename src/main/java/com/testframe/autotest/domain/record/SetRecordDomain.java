package com.testframe.autotest.domain.record;

import com.testframe.autotest.meta.bo.SceneRecordBo;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.query.RecordQry;

import java.util.List;

public interface SetRecordDomain {

    Long updateSetExeRecord(SetExecuteRecordDto setExecuteRecordDto);

    List<SceneRecordBo> sceneExeRecordWithSetRecord(Long setRecordId);

    List<SetExecuteRecordDto> setExeRecord(Long setId, RecordQry recordQry);
}
