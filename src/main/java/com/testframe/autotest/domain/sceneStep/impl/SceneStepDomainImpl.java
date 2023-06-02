package com.testframe.autotest.domain.sceneStep.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.meta.Do.StepDetailDo;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.domain.sceneStep.SceneStepDomain;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
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
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;


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


    @Override
    public List<SceneDetailDto> fatherScene(Long sceneId) {
        List<SceneDetailDto> fatherScenes = new ArrayList<>();
        // 当前子场景关联的父场景的步骤id
        List<Long> sonSceneIds = new ArrayList<Long>(){{add(sceneId);}};
        List<StepDetailDo> sonSteps = stepDetailRepository.queryStepBySceneIds(sonSceneIds);
        if (sonSteps.isEmpty()) { // 未被其他任何场景引用
            return Collections.EMPTY_LIST;
        }
        // 当前步骤所关联的场景id
        List<Long> sonStepIds = sonSteps.stream().map(sonStep -> sonStep.getStepId()).collect(Collectors.toList());
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.queryByStepIds(sonStepIds);
        for (SceneStepRelDo sceneStepRelDo : sceneStepRelDos) {
            if (sceneStepRelDo.getSceneId() == sceneId) {
                log.error("[SceneStepDomainImpl:fatherScene] has cycle, sceneId = {}, sceneStepRelDo = {}",
                        sceneStepRelDo, JSON.toJSONString(sceneStepRelDo));
                throw new AutoTestException("存在循环引用");
            }
            SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(sceneStepRelDo.getSceneId());
            SceneDetailDto sceneDetailDto = sceneDetailConvertor.DoToDto(sceneDetailDo);
            fatherScenes.add(sceneDetailDto);
        }
        return fatherScenes;
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
        // 子场景是作为一个步骤保存的，因此需要获得当前子场景的步骤信息
        List<StepDetailDo> sonSteps = stepDetailRepository.queryStepBySceneIds(sonSceneIds);
        if (sonSteps.isEmpty()) { // 未被其他任何场景引用
            return Collections.EMPTY_LIST;
        }
        // 当前步骤所关联的场景id
        List<Long> sonStepIds = sonSteps.stream().map(sonStep -> sonStep.getStepId()).collect(Collectors.toList());
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.queryByStepIds(sonStepIds);
        for (SceneStepRelDo sceneStepRelDo : sceneStepRelDos) {
            if (sceneStepRelDo.getSceneId() == sonSceneId) {
                log.error("[SceneStepDomainImpl:sceneInOther] has cycle, sceneId = {}, sceneStepRelDo = {}",
                        sceneStepRelDo, JSON.toJSONString(sceneStepRelDo));
            }
            fatherSceneIds.add(sceneStepRelDo.getSceneId());
            List<Long> faSceneIds = this.sceneInOther(sceneStepRelDo.getSceneId()); // 判断当前父场景是否是其他场景的步骤（子场景）
            log.info("[SceneStepDomainImpl:sceneInOther] get sceneId {}, father sceneIds {}",
                    sceneStepRelDo.getSceneId(), faSceneIds);
            if (!faSceneIds.isEmpty()) {
                fatherSceneIds.addAll(faSceneIds);
            }
        }
        return fatherSceneIds;
    }
}
