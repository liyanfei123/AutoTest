package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SetRecord;
import com.testframe.autotest.meta.query.RecordQry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SetRecord record);

    int insertSelective(SetRecord record);

    SetRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SetRecord record);

    int updateByPrimaryKey(SetRecord record);

    List<SetRecord> selectBySetId(@Param("setId") Long setId, @Param("recordQry") RecordQry recordQry);
}