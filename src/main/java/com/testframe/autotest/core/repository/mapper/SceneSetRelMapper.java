package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SceneSetRel;

public interface SceneSetRelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneSetRel record);

    int insertSelective(SceneSetRel record);

    SceneSetRel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SceneSetRel record);

    int updateByPrimaryKey(SceneSetRel record);
}