package com.testframe.autotest.domain.record;

import com.testframe.autotest.meta.bo.SceneRecordBo;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;
import com.testframe.autotest.meta.query.RecordQry;

import java.util.HashMap;
import java.util.List;

public interface RecordDomain {

    // 批量获取场景最近一个执行情况
    HashMap<Long, SceneSimpleExecuteDto> listRecSceneSimpleExeRecord(List<Long> sceneIds);

    /**
     * 获取场景的执行记录
     * @param sceneId
     * @param needSet 是否需要执行集的执行结果
     * @param recordQry
     * @return
     */
    List<SceneRecordBo> sceneExeRecord(Long sceneId, Boolean needSet, RecordQry recordQry);

    SceneRecordBo sceneExeRecordDetail(Long sceneRecordId);

    HashMap<Long, List<SceneRecordBo>> listSceneExeRecord(List<Long> sceneIds, Boolean needSet, RecordQry recordQry);

    Long updateSceneExeRecord(SceneExecuteRecordDto sceneExecuteRecordDto,
                              List<StepExecuteRecordDto> stepExecuteRecordDtos);
}
