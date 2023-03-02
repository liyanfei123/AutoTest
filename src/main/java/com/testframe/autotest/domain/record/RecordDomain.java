package com.testframe.autotest.domain.record;

import com.testframe.autotest.meta.bo.SceneRecordBo;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;
import com.testframe.autotest.meta.query.RecordQry;

import java.util.HashMap;
import java.util.List;

public interface RecordDomain {

    // 批量获取场景执行顺序
    HashMap<Long, SceneSimpleExecuteDto> listSceneSimpleExeRecord(List<Long> sceneIds, RecordQry recordQry);

    List<SceneRecordBo> sceneExeRecord(Long sceneId, RecordQry recordQry);
    HashMap<Long, List<SceneRecordBo>> listSceneExeRecord(List<Long> sceneIds, RecordQry recordQry);

    Long updateSceneExeRecord(SceneExecuteRecordDto sceneExecuteRecordDto,
                              List<StepExecuteRecordDto> stepExecuteRecordDtos);
}
