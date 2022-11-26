package com.testframe.autotest.core.repository;


import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.dao.SceneExecuteRecordDao;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SceneExecuteRecordRepository {

    @Autowired
    private SceneExecuteRecordDao sceneExecuteRecordDao;

    @Autowired
    private SceneExecuteRecordConverter sceneExecuteRecordConverter;

    public SceneExecuteRecord getSceneExeRecordById(Long recordId) {
        SceneRecord sceneRecord = sceneExecuteRecordDao.getSceneRecordById(recordId);
        if (sceneRecord == null) {
            return null;
        }
        return sceneExecuteRecordConverter.toSceneExecuteRecord(sceneRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveSceneExecuteRecord(SceneExecuteRecord sceneExecuteRecord) {
        SceneRecord sceneRecord = sceneExecuteRecordConverter.toPo(sceneExecuteRecord);
        return sceneExecuteRecordDao.saveSceneExecuteRecord(sceneRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSceneExecuteRecord(Long recordId, SceneExecuteRecord sceneExecuteRecord) {
        SceneRecord sceneRecord = sceneExecuteRecordConverter.toPo(sceneExecuteRecord);
        sceneRecord.setId(recordId);
        return sceneExecuteRecordDao.updateSceneExecuteRecord(sceneRecord);
    }

    public List<SceneExecuteRecord> querySceneExecuteRecordBySceneId(Long sceneId, PageQry pageQry) {
        List<SceneRecord> sceneRecords = sceneExecuteRecordDao.getSceneRecordsBySceneId(sceneId, pageQry);
        if (CollectionUtils.isEmpty(sceneRecords)) {
            return Collections.EMPTY_LIST;
        }
        List<SceneExecuteRecord> sceneExecuteRecords = sceneRecords.stream().map(sceneExecuteRecordConverter::toSceneExecuteRecord)
                .collect(Collectors.toList());
        return sceneExecuteRecords;
    }


}
