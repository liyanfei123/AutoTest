package com.testframe.autotest.cache.service;

import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.query.RecordQry;

import java.util.HashMap;
import java.util.List;

public interface RecordCacheService {

    /**
     * 场景下的最新一条执行记录
     * 不考虑执行集执行的结果
     * @param sceneIds
     * @return
     */
    HashMap<Long, SceneSimpleExecuteDto> RecSceneSimpleExeRecFromCache(List<Long> sceneIds);

}
