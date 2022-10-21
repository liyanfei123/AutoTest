package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.repository.mapper.SceneRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SceneExecuteRecordDao {

    @Autowired
    private SceneRecordMapper sceneRecordMapper;

    public Long saveSceneExecuteRecord(SceneRecord sceneRecord) {
        Long currentTime = System.currentTimeMillis();
        sceneRecord.setCreateTime(currentTime);
        sceneRecordMapper.insert(sceneRecord);
        return sceneRecord.getId();
    }

    public List<SceneRecord> getSceneRecordsBySceneId(Long sceneId, Long offset, Integer size) {
        return sceneRecordMapper.getRecordBySceneId(sceneId, offset, size);
    }
}
