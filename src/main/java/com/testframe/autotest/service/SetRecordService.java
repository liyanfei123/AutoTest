package com.testframe.autotest.service;

import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.vo.SceneExeRecordVo;

import java.util.List;

public interface SetRecordService {

    List<SetExecuteRecordDto> records(Long setId);

    List<SceneExeRecordVo> recordsBySetId(Long setRecordId);


}
