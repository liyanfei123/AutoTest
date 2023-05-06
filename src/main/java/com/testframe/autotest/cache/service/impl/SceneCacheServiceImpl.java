package com.testframe.autotest.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.core.meta.convertor.CategorySceneConverter;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SceneCacheServiceImpl implements SceneCacheService {

    @Autowired
    private SceneDetailCache sceneDetailCache;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;

    @Override
    public SceneDetailDto getSceneDetailFromCache(Long sceneId) {
        SceneDetailDto sceneDetailDto = sceneDetailCache.getSceneDetail(sceneId);
        if (sceneDetailDto != null) {
            return sceneDetailDto;
        }
        SceneDetailDo sceneDetailDo  = sceneDetailRepository.querySceneById(sceneId);
        if (sceneDetailDo == null || sceneDetailDo.getIsDelete() == 1) {
            return null;
        } else {
            sceneDetailDto = sceneDetailConvertor.DoToDto(sceneDetailDo);
            List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            sceneDetailDto.setStepNum(sceneStepRelDos.size());
            // 获取场景关联类目
            CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(sceneId);
            sceneDetailDto.setCategoryId(categorySceneDo.getCategoryId());
            log.info("[SceneCacheServiceImpl:getSceneDetailFromCache] get scene from db, scene = {}", JSON.toJSONString(sceneDetailDto));
            // 更新缓存
            sceneDetailCache.updateSceneDetail(sceneId, sceneDetailDto);
        }
        return sceneDetailDto;
    }

}
