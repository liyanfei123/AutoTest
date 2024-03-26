package com.testframe.autotest.domain.scene.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.cache.service.CategoryCacheService;
import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.core.enums.CategoryRelEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.*;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.meta.convertor.SceneSetRelConvertor;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.*;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.domain.sceneSet.SceneSetDomain;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.scene.SceneSearchListDto;
import com.testframe.autotest.meta.query.SceneQry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class SceneDomainImpl implements SceneDomain {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private SceneSetRelRepository sceneSetRelRepository;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private CategorySceneRepository categorySceneRepository;
    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;

    @Autowired
    private SceneDetailCache sceneDetailCache;

    @Autowired
    private SceneCacheService sceneCacheService;

    @Autowired
    private CategoryCacheService categoryCacheService;


    @Override
    public Long updateScene(SceneDetailDto sceneDetailDto) {
        try {
            log.info("[SceneDomainImpl:updateScene] params = {}", JSON.toJSONString(sceneDetailDto));
            SceneDo sceneDo = new SceneDo();
            if (sceneDetailDto.getSceneId() != null && sceneDetailDto.getSceneId() > 0) {
                // 更新场景
                SceneDetailDo sceneDetailDo = sceneDetailConvertor.DtoToDo(sceneDetailDto);
                sceneDo.setSceneDetailDo(sceneDetailDo);
                CategorySceneDo existCategorySceneDo = null;
                if (sceneDetailDto.getCategoryId() != null && sceneDetailDto.getCategoryId() > 0) {
                    log.info("[SceneDomainImpl:updateScene] update scene-categoryId, {}", JSON.toJSONString(sceneDetailDto));
                    existCategorySceneDo = categorySceneRepository.queryBySceneId(sceneDetailDto.getSceneId());
                    if (existCategorySceneDo != null &&
                            existCategorySceneDo.getCategoryId() != sceneDetailDto.getCategoryId()) {
                        // 需要更新类目
                        sceneDo.setOldCategoryId(existCategorySceneDo.getCategoryId());
                        existCategorySceneDo.setCategoryId(sceneDetailDto.getCategoryId());
                    } else {
                        existCategorySceneDo = null;
                    }
                }
                sceneDo.setCategorySceneDo(existCategorySceneDo);
                log.info("[SceneDomainImpl:updateScene] update scene, sceneDetailDo = {}, categorySceneDo = {}",
                        JSON.toJSONString(sceneDetailDo), JSON.toJSONString(existCategorySceneDo));
                return sceneDetailRepository.updateScene(sceneDo) ? 0L : -1L;
            } else {
                // 新增场景
                SceneDetailDo sceneDetailDo = sceneDetailConvertor.DtoToDo(sceneDetailDto);
                sceneDetailDo.setIsDelete(0);
                sceneDo.setSceneDetailDo(sceneDetailDo);
                CategorySceneDo categorySceneDo = new CategorySceneDo();
                categorySceneDo.setCategoryId(sceneDetailDto.getCategoryId());
                categorySceneDo.setSceneId(sceneDetailDto.getSceneId());
                sceneDo.setCategorySceneDo(categorySceneDo);
                log.info("[SceneDomainImpl:updateScene] add scene, sceneDetailDo = {}, categorySceneDo = {}",
                        JSON.toJSONString(sceneDetailDo), JSON.toJSONString(categorySceneDo));
                return sceneDetailRepository.saveSceneInit(sceneDo);
            }
        } catch (Exception e) {
            log.error("[SceneDomainImpl:updateScene] update scene error, reason: ", e);
            throw new AutoTestException(e.getMessage());
        }
    }


    private List<SceneDetailDto> batchQuerySceneByIds(List<Long> sceneIds) {
        log.info("[SceneDomainImpl:batchQuerySceneByIds] param = {}", JSON.toJSONString(sceneIds));
        List<SceneDetailDto> sceneDetailDtos = new ArrayList<>();
        for (Long sceneId : sceneIds) {
            SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(sceneId);
            sceneDetailDtos.add(sceneDetailDto);
        }
        log.info("[SceneDomainImpl:batchQuerySceneByIds] sceneIds = {}, result = {}",
                sceneIds, JSON.toJSONString(sceneDetailDtos));
        return sceneDetailDtos;
    }

    @Override
    public Boolean deleteScene(Long sceneId) {
        List<SceneSetRelDo> sceneSetRelDos = sceneSetRelRepository.querySetRelByStepIdOrSceneId(0L, sceneId);
        if (!sceneSetRelDos.isEmpty()) {
            throw new AutoTestException("当前场景已被其他执行集引用");
        }
        if (sceneCacheService.getSceneDetailFromCache(sceneId) == null) {
            return true;
        }
        return sceneDetailRepository.deleteScene(sceneId);
    }


    // 暂时不支持联合查询
    @Override
    public SceneSearchListDto searchScene(SceneQry sceneQry) {
        log.info("[SceneDomainImpl:searchScene] param = {}", JSON.toJSONString(sceneQry));
        SceneSearchListDto sceneSearchListDto = new SceneSearchListDto();
        List<Long> sceneIds = new ArrayList<>();
        List<SceneDetailDo> sceneDetailDos = new ArrayList<>();
        // 搜索优先级
        if (sceneQry.getSceneId() != null && sceneQry.getSceneId() > 0L) {
            sceneIds = Arrays.asList(sceneQry.getSceneId());
            log.info("[SceneDomainImpl:searchScene] by sceneId = {}, sceneIds = {}", sceneQry.getSceneId(), sceneIds);

            // 根据类目搜索，搜索当前目录下的所有场景
        } else if ((sceneQry.getSceneName() == null || sceneQry.getSceneName() == "")
                && sceneQry.getCategoryId() != null) {
            List<CategorySceneDto> categorySceneDtos = categoryCacheService.sceneCategoryRelFromCache(
                    sceneQry.getCategoryId(), sceneQry.getPageQry());
            sceneIds = categorySceneDtos.stream().map(categorySceneDto -> categorySceneDto.getSceneId())
                    .collect(Collectors.toList());
            Long total = categorySceneRepository.countByCategoryId(sceneQry.getCategoryId(), CategoryRelEnum.SCENE.getType());
            sceneSearchListDto.setTotal(total);
            log.info("[SceneDomainImpl:searchScene] by categoryId = {}, sceneIds = {}", sceneQry.getCategoryId(), sceneIds);

            // 根据名称搜索
        } else if (sceneQry.getSceneName() != null && sceneQry.getSceneName() != "") {
            sceneDetailDos = sceneDetailRepository.queryScenes(null,
                    sceneQry.getSceneName(), sceneQry.getCategoryId(), sceneQry.getStatus(), sceneQry.getPageQry());
            sceneIds = sceneDetailDos.stream().map(sceneDetailDo -> sceneDetailDo.getSceneId())
                    .collect(Collectors.toList());
            log.info("[SceneDomainImpl:searchScene] by sceneName = {}, sceneIds = {}", sceneQry.getSceneName(), sceneIds);
        }

        if (sceneIds.isEmpty()) {
            return sceneSearchListDto;
        }
        List<SceneDetailDto> sceneDetailDtos = this.batchQuerySceneByIds(sceneIds);
        log.info("[SceneDomainImpl:searchScene] search result = {}", JSON.toJSONString(sceneDetailDtos));
        sceneSearchListDto.setSceneDetailDtos(sceneDetailDtos);
        return sceneSearchListDto;
    }


}
