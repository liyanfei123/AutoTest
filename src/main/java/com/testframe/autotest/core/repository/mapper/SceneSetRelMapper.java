package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SceneSetRel;
import com.testframe.autotest.core.meta.request.PageQry;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SceneSetRelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneSetRel record);

    int insertSelective(SceneSetRel record);

    SceneSetRel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SceneSetRel record);

    int updateByPrimaryKey(SceneSetRel record);

    SceneSetRel queryRelBySetIdAndSceneId(Long setId, Long sceneId);

    SceneSetRel queryRelBySetIdAndStepId(Long setId, Long stepId);

    /**
     * 根据时间正序排列
     * @param setId
     * @param pageQry
     * @return
     */
//    List<SceneSetRel> queryRelBySetId(Long setId, PageQry pageQry);

    List<SceneSetRel> queryRelBySetIdWithSort(Long setId, Integer sort, PageQry pageQry);

    List<SceneSetRel> queryRelBySetIdWithTypeAndStatus(Long setId, Integer type, Integer status);

    Integer countRelWithSort(Long setId, Integer sort);

    Integer countRelWithType(Long setId, Integer type);
}