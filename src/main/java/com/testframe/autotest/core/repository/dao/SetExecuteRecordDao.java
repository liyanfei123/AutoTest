package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SetRecord;
import com.testframe.autotest.core.repository.mapper.SetRecordMapper;
import com.testframe.autotest.meta.query.RecordQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 执行集执行记录
 */
@Slf4j
@Component
public class SetExecuteRecordDao {

    @Autowired
    private SetRecordMapper setRecordMapper;

    public Long saveSetRecord(SetRecord setRecord) {
        Long currentTime = System.currentTimeMillis();
        setRecord.setCreateTime(currentTime);
        setRecordMapper.insertSelective(setRecord);
        return setRecord.getId();
    }

    public Boolean updateSetRecord(SetRecord setRecord) {
        Long currentTime = System.currentTimeMillis();
        return setRecordMapper.updateByPrimaryKeySelective(setRecord) > 0 ? true : false;
    }

    public List<SetRecord> queryRecordBySetId(Long setId, RecordQry recordQry) {
        List<SetRecord> setRecords = setRecordMapper.selectBySetId(setId, recordQry);
        if (setRecords.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return setRecords;
    }



}
