package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.mapper.SceneRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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

    public List<SceneRecord> getSceneRecordsBySceneId(Long sceneId, PageQry pageQry) {
        List<SceneRecord> sceneRecords = sceneRecordMapper.getRecordBySceneId(sceneId, pageQry);
        if (CollectionUtils.isEmpty(sceneRecords)) {
            return Collections.EMPTY_LIST;
        }
        return sceneRecords;
    }
}
