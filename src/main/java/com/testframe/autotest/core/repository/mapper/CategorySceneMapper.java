package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.CategoryScene;

public interface CategorySceneMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CategoryScene record);

    int insertSelective(CategoryScene record);

    CategoryScene selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CategoryScene record);

    int updateByPrimaryKey(CategoryScene record);
}