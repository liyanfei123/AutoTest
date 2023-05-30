package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.ExeSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExeSetMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExeSet record);

    int insertSelective(ExeSet record);

    ExeSet selectByPrimaryKey(Long id);

    List<ExeSet> selectBySetName(@Param("setName") String setName);

    int updateByPrimaryKeySelective(ExeSet record);

    int updateByPrimaryKey(ExeSet record);
}