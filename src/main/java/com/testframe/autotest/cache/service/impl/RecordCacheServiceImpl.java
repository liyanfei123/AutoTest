package com.testframe.autotest.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.SceneRecordCache;
import com.testframe.autotest.cache.service.RecordCacheService;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.meta.Do.SceneExecuteRecordDo;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.query.RecordQry;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class RecordCacheServiceImpl implements RecordCacheService {

    @Autowired
    private SceneRecordCache sceneRecordCache;

    @Autowired
    private SceneExecuteRecordRepository sceneExecuteRecordRepository;

    @Override
    public HashMap<Long, SceneSimpleExecuteDto> RecSceneSimpleExeRecFromCache(List<Long> sceneIds) {
        HashMap<Long, SceneSimpleExecuteDto> sceneExecuteDtoMap = new HashMap<>();
        for (Long sceneId : sceneIds) {
            SceneSimpleExecuteDto sceneSimpleExecuteDto = sceneRecordCache.getSceneRecExe(sceneId);
            if (sceneSimpleExecuteDto == null) {
                // 回刷缓存
                sceneSimpleExecuteDto = new SceneSimpleExecuteDto();
                RecordQry recordQry = new RecordQry();
                recordQry.setPage(1);
                recordQry.setSize(1);
                recordQry.setType(SceneExecuteEnum.SINGLE.getType());
                List<SceneExecuteRecordDo> sceneExecuteRecordDos = sceneExecuteRecordRepository.querySceneExecuteRecordBySceneId(sceneId, recordQry);
                if (!sceneExecuteRecordDos.isEmpty()) {
                    sceneSimpleExecuteDto = SceneSimpleExecuteDto.DoToDto(sceneExecuteRecordDos.get(0));
                    log.info("[RecordCacheServiceImpl:RecSceneSimpleExeRecFromCache] load db, sceneId = {}, result = {}",
                            JSON.toJSONString(sceneId), JSON.toJSONString(sceneSimpleExecuteDto));
                    sceneRecordCache.updateSceneRecExe(sceneId, sceneSimpleExecuteDto);
                }
            }
            sceneExecuteDtoMap.put(sceneId, sceneSimpleExecuteDto);
        }
        return sceneExecuteDtoMap;
    }
}
