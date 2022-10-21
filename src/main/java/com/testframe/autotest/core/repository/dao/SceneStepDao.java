package com.testframe.autotest.core.repository.dao;


import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.core.repository.mapper.SceneStepMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 场景步骤关联
 */
@Slf4j
@Component
public class SceneStepDao {

    @Autowired
    private SceneStepMapper sceneStepMapper;

    public Boolean saveSceneStep(SceneStep sceneStep) {
        Long currentTime = System.currentTimeMillis();
        sceneStep.setCreateTime(currentTime);
        sceneStep.setUpdateTime(currentTime);
        return sceneStepMapper.insert(sceneStep) > 0 ? true : false;
    }

    public Boolean updateSceneStep(SceneStep sceneStep) {
        Long currentTime = System.currentTimeMillis();
        sceneStep.setUpdateTime(currentTime);
        return sceneStepMapper.updateByPrimaryKey(sceneStep) > 0 ? true : false;
    }

    public SceneStep queryById(Long id) {
        return sceneStepMapper.selectByPrimaryKey(id);
    }

    public List<SceneStep> queryBySceneId(Long sceneId) {
        return sceneStepMapper.queryStepsBySceneId(sceneId);
    }

}
