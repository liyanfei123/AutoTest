package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.StepDetail;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StepDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StepDetail record);

    int insertSelective(StepDetail record);

    StepDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StepDetail record);

    int updateByPrimaryKey(StepDetail record);

    List<StepDetail> queryStepByIds(@Param("stepIds") List<Long> stepIds);
}