package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.StepOrder;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StepOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StepOrder record);

    int insertSelective(StepOrder record);

    StepOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StepOrder record);

    int updateByPrimaryKey(StepOrder record);

    List<StepOrder> getStepOrderBySceneId(@Param("sceneId") Long sceneId);

    StepOrder getStepOrderByRecordId(@Param("recordId") Long sceneId);
}