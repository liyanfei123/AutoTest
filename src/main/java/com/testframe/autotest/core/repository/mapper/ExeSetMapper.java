package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.ExeSet;

public interface ExeSetMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExeSet record);

    int insertSelective(ExeSet record);

    ExeSet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExeSet record);

    int updateByPrimaryKey(ExeSet record);
}