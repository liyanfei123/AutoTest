package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.domain.record.SetRecordDomain;
import com.testframe.autotest.meta.bo.SceneRecordBo;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.query.RecordQry;
import com.testframe.autotest.meta.vo.SceneExeRecordVo;
import com.testframe.autotest.service.SceneRecordService;
import com.testframe.autotest.service.SetRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SetRecordServiceImpl implements SetRecordService {

    @Autowired
    private SetRecordDomain setRecordDomain;

    @Autowired
    private SceneRecordService sceneRecordService;

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Override
    public List<SetExecuteRecordDto> records(Long setId) {
        RecordQry recordQry = new RecordQry();
        recordQry.setOffset(0);
        recordQry.setSize(autoTestConfig.getRecordSize()); // 固定20条记录
        List<SetExecuteRecordDto> setExecuteRecordDtos = setRecordDomain.setExeRecord(setId, recordQry);
        return setExecuteRecordDtos;
    }

    @Override
    public List<SceneExeRecordVo> recordsBySetId(Long setRecordId) {
        log.info("[SetRecordServiceImpl:recordsBySetId] query execute records by setRecordId {}", setRecordId);
        List<SceneRecordBo> sceneRecordBos = setRecordDomain.sceneExeRecordWithSetRecord(setRecordId);
        if (sceneRecordBos.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        SceneRecordServiceImpl sceneRecordServiceImpl = new SceneRecordServiceImpl();
        List<SceneExeRecordVo> sceneExeRecordVos = sceneRecordServiceImpl.buildSceneExeRecordVos(sceneRecordBos);
        return sceneExeRecordVos;
    }
}
