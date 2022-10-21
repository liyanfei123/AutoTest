package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SceneDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SceneDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneDetail record);

    int insertSelective(SceneDetail record);

    SceneDetail selectByPrimaryKey(Long id);

    SceneDetail selectByTitle(@Param("title") String title);

    int updateByPrimaryKeySelective(SceneDetail record);

    int updateByPrimaryKey(Long id);
}