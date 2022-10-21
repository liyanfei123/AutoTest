package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.StepRecord;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StepRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StepRecord record);

    int insertSelective(StepRecord record);

    StepRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StepRecord record);

    int updateByPrimaryKey(StepRecord record);
}