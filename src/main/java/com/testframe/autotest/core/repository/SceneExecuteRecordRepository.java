package com.testframe.autotest.core.repository;


import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.SceneExecuteRecordDo;
import com.testframe.autotest.core.meta.Do.StepExecuteRecordDo;
import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.meta.convertor.StepExecuteRecordConverter;
import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.meta.po.StepRecord;
import com.testframe.autotest.core.repository.dao.SceneExecuteRecordDao;
import com.testframe.autotest.core.repository.dao.StepExecuteRecordDao;
import com.testframe.autotest.meta.query.RecordQry;
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
    private StepExecuteRecordDao stepExecuteRecordDao;

    @Autowired
    private SceneExecuteRecordConverter sceneExecuteRecordConverter;

    @Autowired
    private StepExecuteRecordConverter stepExecuteRecordConverter;

    public SceneExecuteRecordDo getSceneExeRecordById(Long recordId) {
        SceneRecord sceneRecord = sceneExecuteRecordDao.getSceneRecordById(recordId);
        if (sceneRecord == null) {
            return null;
        }
        return sceneExecuteRecordConverter.PoToDo(sceneRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveSceneExecuteRecord(SceneExecuteRecordDo sceneExecuteRecordDo) {
        SceneRecord sceneRecord = sceneExecuteRecordConverter.DoToPo(sceneExecuteRecordDo);
        return sceneExecuteRecordDao.saveSceneExecuteRecord(sceneRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSceneExecuteRecord(SceneExecuteRecordDo sceneExecuteRecordDo,
                                            List<StepExecuteRecordDo> stepExecuteRecordDos) {
        SceneRecord sceneRecord = sceneExecuteRecordConverter.DoToPo(sceneExecuteRecordDo);
        if (!sceneExecuteRecordDao.updateSceneExecuteRecord(sceneRecord)) {
            return false;
        }
        if (stepExecuteRecordDos != null) {
            List<StepRecord> stepRecords = stepExecuteRecordDos.stream().map(stepExecuteRecordConverter::DoToPo)
                            .collect(Collectors.toList());
            if (!stepExecuteRecordDao.batchSaveStepExecuteRecord(stepRecords)) {
                throw new AutoTestException("步骤执行记录保存错误");
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSceneAndStepExecuteRecord(Long recordId) {
        // 同时更新场景记录和步骤执行记录
        return true;
    }

    public List<SceneExecuteRecordDo> querySceneExecuteRecordBySceneId(Long sceneId, RecordQry recordQry) {
        List<SceneRecord> sceneRecords = sceneExecuteRecordDao.getSceneRecordsBySceneId(sceneId, recordQry);
        if (CollectionUtils.isEmpty(sceneRecords)) {
            return Collections.EMPTY_LIST;
        }
        List<SceneExecuteRecordDo> sceneExecuteRecords = sceneRecords.stream().map(sceneExecuteRecordConverter::PoToDo)
                .collect(Collectors.toList());
        return sceneExecuteRecords;
    }


}
