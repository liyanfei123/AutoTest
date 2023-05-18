package com.testframe.autotest.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.SceneStepRelCache;
import com.testframe.autotest.cache.ao.StepDetailCache;
import com.testframe.autotest.cache.service.StepCacheService;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.meta.Do.StepDetailDo;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StepCacheServiceImpl implements StepCacheService {

    @Autowired
    private SceneStepRelCache sceneStepRelCache;

    @Autowired
    private StepDetailCache stepDetailCache;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Override
    public StepDetailDto getStepInfoFromCache(Long stepId) {
        StepDetailDto stepDetailDto = stepDetailCache.getStepDetail(stepId);
        if (stepDetailDto != null) {
            log.info("[StepCacheServiceImpl:getStepInfoFromCache] stepInfo load stepInfo from cache, stepId = {}, stepInfo = {}",
                    stepId, JSON.toJSONString(stepDetailDto));
            return stepDetailDto;
        }
        stepDetailDto = new StepDetailDto();
        // 更新缓存
        StepDetailDo stepDetailDo = stepDetailRepository.queryStepById(stepId);
        if (stepDetailDo == null) {
            return null;
        }
        SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepId(stepId);
        stepDetailDto.setSceneId(sceneStepRelDo.getSceneId());
        stepDetailDto.setStepId(stepId);
        stepDetailDto.setSonSceneId(stepDetailDo.getSonSceneId());
        stepDetailDto.setStepName(stepDetailDo.getStepName());
        stepDetailDto.setStepStatus(sceneStepRelDo.getStatus());
        stepDetailDto.setType(sceneStepRelDo.getType());
        stepDetailDto.setStepUIInfo(stepDetailDo.getStepInfo());
        stepDetailCache.updateStepDetail(stepId, stepDetailDto);
        log.info("[StepCacheServiceImpl:stepInfo] refresh stepInfo cache from db, stepId = {} stepInfo = {}",
                stepId, JSON.toJSONString(stepDetailDto));
        return stepDetailDto;
    }

    @Override
    public HashMap<Long, StepDetailDto> getStepInfosFromCache(List<Long> stepIds) {
        HashMap<Long, StepDetailDto> stepDetailDtoMap = new HashMap<>();
        for (Long stepId : stepIds) {
            stepDetailDtoMap.put(stepId, this.getStepInfoFromCache(stepId));
        }
        return stepDetailDtoMap;
    }

    @Override
    public List<StepDetailDto> getStepInSceneFromCache(Long sceneId) {
        List<StepDetailDto> stepDetailDtos = new ArrayList<>();
        HashMap<Long, StepDetailDto> stepDetailDtoMap = sceneStepRelCache.getSceneStepRels(sceneId);
        if (stepDetailDtoMap != null
                && !stepDetailDtoMap.values().isEmpty()) { // 避免缓存出现空的情况
            stepDetailDtos = new ArrayList<StepDetailDto>(stepDetailDtoMap.values());
            log.info("[StepCacheServiceImpl:getStepInSceneFromCache] get step in scene from cache, sceneId = {}, result = {}",
                    sceneId, JSON.toJSONString(stepDetailDtos));
        } else {
            // 无缓存
            List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            if (sceneStepRelDos.isEmpty()) { // 当前场景下无步骤
                return stepDetailDtos;
            }
            List<Long> stepIds = sceneStepRelDos.stream().map(sceneStepRelDo -> sceneStepRelDo.getStepId())
                    .collect(Collectors.toList());
            for (Long stepId : stepIds) {
                StepDetailDto stepDetailDto = this.getStepInfoFromCache(stepId);
                stepDetailDtos.add(stepDetailDto);
            }
            // 回刷缓存
            HashMap<Long, StepDetailDto> stepDetailDtoMapFormDb = new HashMap<Long, StepDetailDto>();
            stepDetailDtos.stream().forEach(stepDetailDto ->
                    stepDetailDtoMapFormDb.put(stepDetailDto.getStepId(), stepDetailDto));
            log.info("[StepCacheServiceImpl:getStepInSceneFromCache] refresh cache info {}",
                    JSON.toJSONString(stepDetailDtoMapFormDb));
            sceneStepRelCache.updateSceneStepRels(sceneId, stepDetailDtoMapFormDb);
        }
        return stepDetailDtos;
    }


}
