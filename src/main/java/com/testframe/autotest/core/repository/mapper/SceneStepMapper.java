package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SceneStep;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SceneStepMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneStep record);

    int insertSelective(SceneStep record);

    SceneStep selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SceneStep record);

    int updateByPrimaryKey(SceneStep record);

    List<SceneStep> queryStepsBySceneId(@Param("sceneId") Long sceneId);
}