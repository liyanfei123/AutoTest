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

    Boolean batchUpdateStepStatus(@Param("status") Integer status,
                          @Param("updateTime") Long updateTime, @Param("ids") List<Long> ids);

    List<SceneStep> queryStepsBySceneId(@Param("sceneId") Long sceneId);

    SceneStep queryStepByStepId(@Param("stepId") Long stepId);

    SceneStep queryStepByStepIdAndSceneId(@Param("stepId") Long stepId, @Param("sceneId") Long sceneId);

    List<SceneStep> queryStepByStepIds(@Param("stepIds") List<Long> stepIds);
}