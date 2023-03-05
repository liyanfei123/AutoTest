package com.testframe.autotest.domain.sceneStep.impl;

import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.meta.Do.StepDetailDo;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.domain.sceneStep.SceneStepDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2023/03/05 19:20
 * @author: lyf
 */
@Slf4j
@Service
@Component
public class SceneStepDomainImpl implements SceneStepDomain {

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;


    /**
     * 判断当前场景下的子步骤是否存在循环引用
     * @param sceneId
     * @param sonSceneId
     * @return
     */
    @Override
    public Boolean sceneIncludeSelf(Long sceneId, Long sonSceneId) {
        return null;
    }

    /**
     * 获取当前场景作为子场景所被关联的其余父场景
     * @param sonSceneIds
     * @return
     */
    @Override
    public Map<Long, List<Long>> scenesInOther(List<Long> sonSceneIds) {
        Map<Long, List<Long>> fatherSceneMap = new HashMap<>();
        if (sonSceneIds.isEmpty()) {
            return fatherSceneMap;
        }
        for (Long sonSceneId : sonSceneIds) {
            List<Long> fatherSceneIds = this.sceneInOther(sonSceneId);
            fatherSceneMap.put(sonSceneId, fatherSceneIds);
        }
        return fatherSceneMap;
    }

    private List<Long> sceneInOther(Long sonSceneId) {
        List<Long> fatherSceneIds = new ArrayList<>();
        // 当前子场景关联的父场景的步骤id
        List<Long> sonSceneIds = new ArrayList<Long>(){{add(sonSceneId);}};
        List<StepDetailDo> sonSteps = stepDetailRepository.queryStepBySceneIds(sonSceneIds);
        if (sonSteps.isEmpty()) { // 未被其他任何场景引用
            return Collections.EMPTY_LIST;
        }
        // 当前子场景所关联的父场景id
        List<Long> sonStepIds = sonSteps.stream().map(sonStep -> sonStep.getStepId()).collect(Collectors.toList());
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.queryByStepIds(sonStepIds);
        for (SceneStepRelDo sceneStepRelDo : sceneStepRelDos) {
            fatherSceneIds.add(sceneStepRelDo.getSceneId());
            List<Long> faSceneIds = this.sceneInOther(sceneStepRelDo.getSceneId()); // 判断当前父场景是否是其他场景的步骤（子场景）
            if (faSceneIds.isEmpty()) {
                return Collections.EMPTY_LIST;
            } else {
                fatherSceneIds.add(sceneStepRelDo.getSceneId());
                fatherSceneIds.addAll(faSceneIds);
            }
        }
        return fatherSceneIds;
    }
}
