package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.enums.ExeOrderEnum;
import com.testframe.autotest.core.meta.Do.SceneSetRelDo;
import com.testframe.autotest.core.meta.convertor.SceneSetRelConvertor;
import com.testframe.autotest.core.meta.po.SceneSetRel;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.dao.SceneSetRelDao;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.query.SceneSetRelQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SceneSetRelRepository {

    @Autowired
    private SceneSetRelDao sceneSetRelDao;

    @Autowired
    private SceneSetRelConvertor sceneSetRelConvertor;

    // 返回未添加成功的场景
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateSceneSetRelWithScenes(List<SceneSetRelDo> sceneSetRelDos) {
        List<Long> failSceneIds = new ArrayList<>();
        List<SceneSetRel>  sceneSetRels = sceneSetRelDos.stream().map(sceneSetRelDo ->
                sceneSetRelConvertor.DoToPo(sceneSetRelDo)).collect(Collectors.toList());
        for (SceneSetRel sceneSetRel : sceneSetRels) {
            Long relId = sceneSetRelDao.updateSceneSetRel(sceneSetRel);
            if (relId == 0L) {
                failSceneIds.add(sceneSetRel.getSceneId());
            }
        }
        return failSceneIds;
    }

    // 返回未添加成功的步骤
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateSceneSetRelWithSteps(List<SceneSetRelDo> sceneSetRelDos) {
        List<Long> failStepIds = new ArrayList<>();
        List<SceneSetRel>  sceneSetRels = sceneSetRelDos.stream().map(sceneSetRelDo ->
                sceneSetRelConvertor.DoToPo(sceneSetRelDo)).collect(Collectors.toList());
        for (SceneSetRel sceneSetRel : sceneSetRels) {
            Long relId = sceneSetRelDao.updateSceneSetRel(sceneSetRel);
            if (relId == 0L) {
                failStepIds.add(sceneSetRel.getStepId());
            }
        }
        return failStepIds;
    }


    public Boolean deleteSceneSetRel(Long setId, Long sceneId, Long stepId) {
        if (sceneId > 0) {
            return this.deleteSceneSetRelWithSceneId(setId, sceneId);
        } else if (stepId > 0)  {
            return this.deleteSceneSetRelWithStepId(setId, stepId);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSceneSetRelWithSceneId(Long setId, Long sceneId) {
        SceneSetRel sceneSetRel = sceneSetRelDao.querySetRelBySceneId(setId, sceneId);
        if (sceneSetRel == null) {
            return null;
        }
        sceneSetRel.setIsDelete(1);
        return sceneSetRelDao.updateSceneSetRel(sceneSetRel) > 0L ? true : false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSceneSetRelWithStepId(Long setId, Long stepId) {
        SceneSetRel sceneSetRel = sceneSetRelDao.querySetRelByStepId(setId, stepId);
        if (sceneSetRel == null) {
            return false;
        }
        sceneSetRel.setIsDelete(1);
        return sceneSetRelDao.updateSceneSetRel(sceneSetRel) > 0L ? true : false;
    }

    // 通过setId和sceneId/stepId只能找到一条记录
    public SceneSetRelDo querySceneIdSetRel(SceneSetRelQry sceneSetRelQry) {
        SceneSetRel sceneSetRel = null;
        if (sceneSetRelQry.getSceneId() != null && sceneSetRelQry.getSceneId() > 0L) {
            sceneSetRel = sceneSetRelDao.querySetRelBySceneId(
                    sceneSetRelQry.getSetId(), sceneSetRelQry.getSceneId());
        } else if (sceneSetRelQry.getStepId() != null && sceneSetRelQry.getStepId() > 0L) {
            sceneSetRel = sceneSetRelDao.querySetRelByStepId(
                    sceneSetRelQry.getSetId(), sceneSetRelQry.getStepId());
        }
        if (sceneSetRel != null) {
            return sceneSetRelConvertor.PoToDo(sceneSetRel);
        } else {
            return null;
        }
    }

    public SceneSetRelDo querySceneSetRelById(Long relId) {
        SceneSetRel sceneSetRel = sceneSetRelDao.queryRelById(relId);
        if (sceneSetRel == null) {
            return null;
        }
        return sceneSetRelConvertor.PoToDo(sceneSetRel);
    }

    public List<SceneSetRelDo> querySetRelBySetId(Long setId, Integer sort, PageQry pageQry) {
        List<SceneSetRel> sceneSetRels = sceneSetRelDao.querySetRelBySetId(setId, sort, pageQry);
        if (sceneSetRels.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SceneSetRelDo> sceneSetRelDos = sceneSetRels.stream().map(sceneSetRel ->
                sceneSetRelConvertor.PoToDo(sceneSetRel)).collect(Collectors.toList());
        return sceneSetRelDos;
    }

    public List<SceneSetRelDo> querySetRelByStepIdOrSceneId(Long stepId, Long sceneId) {
        List<SceneSetRel> sceneSetRels = new ArrayList<>();
        if (stepId > 0) {
            sceneSetRels = sceneSetRelDao.queryRelByStepId(stepId);
        } else if (sceneId > 0) {
            sceneSetRels = sceneSetRelDao.queryRelBySceneId(sceneId);
        }
        List<SceneSetRelDo> sceneSetRelDos = sceneSetRels.stream().map(sceneSetRel ->
                sceneSetRelConvertor.PoToDo(sceneSetRel)).collect(Collectors.toList());
        return sceneSetRelDos;
    }

    public HashMap<Integer, List<SceneSetRelDo>> querySetRelBySetIdWithTypeAndStatus(Long setId, Integer type, Integer status) {
        List<SceneSetRel> sceneSetRels = sceneSetRelDao.queryRelBySetIdWithTypeAndStatus(setId, type, status);
        HashMap<Integer, List<SceneSetRelDo>> diffSortRelMap = new HashMap<>();
        if (sceneSetRels.isEmpty()) {
            return diffSortRelMap;
        }
        List<SceneSetRelDo> sceneSetRelDos = sceneSetRels.stream().map(sceneSetRel ->
                sceneSetRelConvertor.PoToDo(sceneSetRel)).collect(Collectors.toList());
        List<SceneSetRelDo> headRels = sceneSetRelDos.stream().filter(
                sceneSetRelDo -> sceneSetRelDo.getSort() == ExeOrderEnum.HEAD.getType()).collect(Collectors.toList());
        List<SceneSetRelDo> normalRels = sceneSetRelDos.stream().filter(
                sceneSetRelDo -> sceneSetRelDo.getSort() == ExeOrderEnum.NORMAL.getType()).collect(Collectors.toList());
        List<SceneSetRelDo> lastRels = sceneSetRelDos.stream().filter(
                sceneSetRelDo -> sceneSetRelDo.getSort() == ExeOrderEnum.LAST.getType()).collect(Collectors.toList());
        diffSortRelMap.put(ExeOrderEnum.HEAD.getType(), headRels);
        diffSortRelMap.put(ExeOrderEnum.NORMAL.getType(), normalRels);
        diffSortRelMap.put(ExeOrderEnum.LAST.getType(), lastRels);
        return diffSortRelMap;
    }

    public List<SceneSetRelDo> querySceneIdSetRels(List<SceneSetRelQry> sceneSetRelQries) {
        List<SceneSetRelDo> sceneSetRelDos = new ArrayList<>();
        for (SceneSetRelQry sceneSetRelQry : sceneSetRelQries) {
            SceneSetRelDo sceneSetRelDo = this.querySceneIdSetRel(sceneSetRelQry);
            if (sceneSetRelDo != null) {
                sceneSetRelDos.add(sceneSetRelDo);
            }
        }
        return sceneSetRelDos;
    }

    public Integer countSetRelBySetId(Long setId, Integer sort) {
        return sceneSetRelDao.countSetRelBySort(setId, sort);
    }

    public Integer countSetRelBySetIdWithType(Long setId, Integer type) {
        return sceneSetRelDao.countSetRelByType(setId, type);
    }


}
