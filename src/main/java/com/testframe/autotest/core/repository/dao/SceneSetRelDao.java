package com.testframe.autotest.core.repository.dao;


import com.testframe.autotest.core.meta.po.SceneSetRel;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.mapper.SceneSetRelMapper;
import com.testframe.autotest.meta.query.SceneSetRelQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SceneSetRelDao {

    @Autowired
    private SceneSetRelMapper sceneSetRelMapper;

    public Long updateSceneSetRel(SceneSetRel sceneSetRel) {
        Long curr = System.currentTimeMillis();
        if (sceneSetRel.getId() == null || sceneSetRel.getId() <= 0) {
            // 新增
            sceneSetRel.setCreateTime(curr);
            sceneSetRel.setUpdateTime(curr);
            sceneSetRel.setIsDelete(0);
            if (sceneSetRelMapper.insertSelective(sceneSetRel) > 0) {
                return sceneSetRel.getId();
            }
        } else {
            // 更新
            sceneSetRel.setUpdateTime(curr);
            return sceneSetRelMapper.updateByPrimaryKeySelective(sceneSetRel) > 0 ? sceneSetRel.getId() : 0L;
        }
        return 0L;
    }

    public SceneSetRel querySetRelBySceneId(Long setId, Long sceneId) {
        return sceneSetRelMapper.queryRelBySetIdAndSceneId(setId, sceneId);
    }

    public SceneSetRel querySetRelByStepId(Long setId, Long stepId) {
        return sceneSetRelMapper.queryRelBySetIdAndStepId(setId, stepId);
    }

    public List<SceneSetRel> querySetRelBySetId(Long setId, Integer sort, PageQry pageQry) {
        return sceneSetRelMapper.queryRelBySetIdWithSort(setId, sort, pageQry);
    }

    public List<SceneSetRel> queryRelBySetIdWithTypeAndStatus(Long setId, Integer type, Integer status) {
        return sceneSetRelMapper.queryRelBySetIdWithTypeAndStatus(setId, type, status);
    }

    public List<SceneSetRel> queryRelByStepId(Long stepId) {
        return sceneSetRelMapper.queryRelByStepId(stepId);
    }

    public List<SceneSetRel> queryRelBySceneId(Long sceneId) {
        return sceneSetRelMapper.queryRelBySceneId(sceneId);
    }

    public Integer countSetRelBySort(Long setId, Integer sort) {
        return sceneSetRelMapper.countRelWithSort(setId, sort);
    }

    public Integer countSetRelByType(Long setId, Integer type) {
        return sceneSetRelMapper.countRelWithType(setId, type);
    }



}
