package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.repository.mapper.SceneRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SceneExecuteRecordDao {

    @Autowired
    private SceneRecordMapper sceneRecordMapper;

    public Boolean saveSceneExecuteRecord(SceneRecord sceneRecord) {
        Long currentTime = System.currentTimeMillis();
        sceneRecord.setCreateTime(currentTime);
        return sceneRecordMapper.insert(sceneRecord) > 0 ? true : false;
    }
}
