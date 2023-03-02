package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.repository.mapper.SceneRecordMapper;
import com.testframe.autotest.meta.query.RecordQry;
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
        sceneRecordMapper.insertSelective(sceneRecord);
        return sceneRecord.getId();
    }

    public Boolean updateSceneExecuteRecord(SceneRecord sceneRecord) {
        return sceneRecordMapper.updateByPrimaryKeySelective(sceneRecord) > 0 ? true : false;
    }

    public List<SceneRecord> getSceneRecordsBySceneId(Long sceneId, RecordQry recordQry) {
        List<SceneRecord> sceneRecords = sceneRecordMapper.getRecordBySceneId(sceneId, recordQry);
        if (CollectionUtils.isEmpty(sceneRecords)) {
            return Collections.EMPTY_LIST;
        }
        return sceneRecords;
    }

    public SceneRecord getSceneRecordById(Long recordId) {
        SceneRecord sceneRecord = sceneRecordMapper.selectByPrimaryKey(recordId);
        return sceneRecord;
    }

    public Boolean delSceneRecord(Long recordId) {
        return sceneRecordMapper.deleteByPrimaryKey(recordId) > 0 ? true : false;
    }

}
