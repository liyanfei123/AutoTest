package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SceneRecord;

import com.testframe.autotest.core.meta.request.PageQry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SceneRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneRecord record);

    int insertSelective(SceneRecord record);

    SceneRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SceneRecord record);

    int updateByPrimaryKey(SceneRecord record);

    List<SceneRecord> getRecordBySceneId(@Param("sceneId") Long sceneId, @Param("pageQry") PageQry pageQry);
}