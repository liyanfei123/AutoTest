package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.request.PageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SceneDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneDetail record);

    int insertSelective(SceneDetail record);

    SceneDetail selectByPrimaryKey(Long id);

    List<SceneDetail> selectByTitle(@Param("title") String title);

    List<SceneDetail> listBySceneIds(@Param("sceneIds") List<Long> sceneIds);

    int updateByPrimaryKeySelective(SceneDetail record);

    int updateByPrimaryKey(Long id);

    Long countScenes(@Param("sceneId") Long sceneId, @Param("sceneName") String sceneName);

    List<SceneDetail> queryScenesBySceneIds(@Param("sceneIds") List<Integer> sceneIds, @Param("pageQry") PageQry pageQry);

    List<SceneDetail> queryScenes(@Param("pageQry") PageQry pageQry);

    List<SceneDetail> queryScenesLikeTitle(@Param("sceneName") String sceneName, @Param("pageQry") PageQry pageQry);

    List<SceneDetail> queryScenesLikeTitleInCategory(@Param("sceneName") String sceneName, @Param("categoryId") Integer categoryId,
                                                     @Param("pageQry") PageQry pageQry);
}