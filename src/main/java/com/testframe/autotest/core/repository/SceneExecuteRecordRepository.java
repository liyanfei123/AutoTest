package com.testframe.autotest.core.repository;


import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.repository.dao.SceneExecuteRecordDao;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SceneExecuteRecordRepository {

    @Autowired
    private SceneExecuteRecordDao sceneExecuteRecordDao;

    @Autowired
    private SceneExecuteRecordConverter sceneExecuteRecordConverter;

    @Transactional(rollbackFor = Exception.class)
    public Long saveSceneExecuteRecord(SceneExecuteRecord sceneExecuteRecord) {
        SceneRecord sceneRecord = sceneExecuteRecordConverter.toPo(sceneExecuteRecord);
        return sceneExecuteRecordDao.saveSceneExecuteRecord(sceneRecord);
    }

    public List<SceneExecuteRecord> querySceneExecuteRecordBySceneId(Long sceneId, Long offset, Integer size) {
        List<SceneRecord> sceneRecords = sceneExecuteRecordDao.getSceneRecordsBySceneId(sceneId, offset, size);
        List<SceneExecuteRecord> sceneExecuteRecords = sceneRecords.stream().map(sceneExecuteRecordConverter::toSceneExecuteRecord)
                .collect(Collectors.toList());
        return sceneExecuteRecords;
    }


}
