package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.meta.Do.SetExecuteRecordDo;
import com.testframe.autotest.core.meta.convertor.SetExecuteRecordConvertor;
import com.testframe.autotest.core.meta.po.SetRecord;
import com.testframe.autotest.core.repository.dao.SetExecuteRecordDao;
import com.testframe.autotest.meta.query.RecordQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SetExecuteRecordRepository {

    @Autowired
    private SetExecuteRecordDao setExecuteRecordDao;

    @Autowired
    private SetExecuteRecordConvertor setExecuteRecordConvertor;

    @Transactional(rollbackFor = Exception.class)
    public Long saveSetExecuteRecord(SetExecuteRecordDo setExecuteRecordDo) {
        SetRecord setRecord = setExecuteRecordConvertor.DoToPo(setExecuteRecordDo);
        Long setRecordId = setExecuteRecordDao.saveSetRecord(setRecord);
        return setRecordId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSetExecuteRecord(SetExecuteRecordDo setExecuteRecordDo) {
        SetRecord setRecord = setExecuteRecordConvertor.DoToPo(setExecuteRecordDo);
        return setExecuteRecordDao.updateSetRecord(setRecord);
    }

    public List<SetExecuteRecordDo> querySetExeRecordBySetId(Long setId, RecordQry recordQry) {
        List<SetRecord> setRecords = setExecuteRecordDao.queryRecordBySetId(setId, recordQry);
        if (setRecords.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SetExecuteRecordDo> setExecuteRecordDos = setRecords.stream().map(
                setRecord -> setExecuteRecordConvertor.PoToDo(setRecord)).collect(Collectors.toList());
        return setExecuteRecordDos;
    }
}
