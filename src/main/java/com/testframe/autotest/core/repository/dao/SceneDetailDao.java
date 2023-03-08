package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.mapper.SceneDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class SceneDetailDao {

    @Autowired
    private SceneDetailMapper sceneDetailMapper;

    public List<SceneDetail> querySceneByTitle(String title) {
        List<SceneDetail> sceneDetails = sceneDetailMapper.selectByTitle(title);
        if (CollectionUtils.isEmpty(sceneDetails)) {
            return Collections.EMPTY_LIST;
        }
        return sceneDetails;
    }

    public List<SceneDetail> querySceneLikeTitle(String sceneName, PageQry pageQry) {
        List<SceneDetail> sceneDetailList = sceneDetailMapper.queryScenesLikeTitle(sceneName, pageQry);
        if (CollectionUtils.isEmpty(sceneDetailList)) {
            return Collections.EMPTY_LIST;
        }
        return sceneDetailList;
    }

    public List<SceneDetail> querySceneLikeTitleInCategory(String sceneName, Integer category,
                                                           PageQry pageQry) {
        List<SceneDetail> sceneDetailList = sceneDetailMapper.queryScenesLikeTitleInCategory(sceneName, category, pageQry);
        if (CollectionUtils.isEmpty(sceneDetailList)) {
            return Collections.EMPTY_LIST;
        }
        return sceneDetailList;
    }


    public Long saveScene(SceneDetail sceneDetail) {
        Long currentTime = System.currentTimeMillis();
        sceneDetail.setCreateTime(currentTime);
        sceneDetail.setUpdateTime(currentTime);
        if (sceneDetailMapper.insertSelective(sceneDetail) > 0) {
            return sceneDetail.getId();
        };
        return 0L;
    }

    public Boolean updateScene(SceneDetail sceneDetail) {
        Long currentTime = System.currentTimeMillis();
        sceneDetail.setUpdateTime(currentTime);
        return sceneDetailMapper.updateByPrimaryKeySelective(sceneDetail) > 0;
    }

    public SceneDetail querySceneById(Long sceneId) {
        SceneDetail sceneDetail = sceneDetailMapper.selectByPrimaryKey(sceneId);
        return sceneDetail;
    }

    public List<SceneDetail> batchQuerySceneByIds(List<Long> sceneIds) {
        List<SceneDetail> sceneDetails = sceneDetailMapper.listBySceneIds(sceneIds);
        if (CollectionUtils.isEmpty(sceneDetails)) {
            return Collections.EMPTY_LIST;
        }
        return sceneDetails;
    }

    public List<SceneDetail> queryScenes(PageQry pageQry) {
        List<SceneDetail> sceneDetails = sceneDetailMapper.queryScenes(pageQry);
        if (CollectionUtils.isEmpty(sceneDetails)) {
            return Collections.EMPTY_LIST;
        }
        return sceneDetails;
    }

    public Long countScenes(Long sceneId, String sceneName) {
        return sceneDetailMapper.countScenes(sceneId, sceneName);
    }
}
