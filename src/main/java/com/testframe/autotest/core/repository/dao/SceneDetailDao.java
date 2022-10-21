package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.core.repository.mapper.SceneDetailMapper;
import com.testframe.autotest.meta.bo.Scene;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SceneDetailDao {

    @Autowired
    private SceneDetailMapper sceneDetailMapper;

    public Boolean querySceneByTitle(String title) {
        if (sceneDetailMapper.selectByTitle(title) == null) {
            return false;
        }
        return true;
    }

    public Long saveScene(SceneDetail sceneDetail) {
        Long currentTime = System.currentTimeMillis();
        sceneDetail.setCreateTime(currentTime);
        sceneDetail.setUpdateTime(currentTime);
        if (sceneDetailMapper.insert(sceneDetail) > 0) {
            return sceneDetail.getId();
        };
        return null;
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

}
