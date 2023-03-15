package com.testframe.autotest.domain.scene.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.CategoryCache;
import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.*;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.scene.SceneSearchListDto;
import com.testframe.autotest.meta.query.SceneQry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private CategoryCache categoryCache;

    @Autowired
    private CategorySceneRepository categorySceneRepository;
    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;

    @Autowired
    private SceneDetailCache sceneDetailCache;


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
                CategorySceneDo categorySceneDo = new CategorySceneDo(null, sceneDetailDto.getCategoryId(), null, null, null);
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

    @Override
    public SceneDetailDto getSceneById(Long sceneId) {
        log.info("[SceneDomainImpl:getSceneById] param = {}", sceneId);
        // 读取缓存
        SceneDetailDto sceneDetailDto = sceneDetailCache.getSceneDetail(sceneId);
        if (sceneDetailDto != null) {
            return sceneDetailDto;
        }
        // 读取db
        sceneDetailDto = new SceneDetailDto();
        SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(sceneId);
        if (sceneDetailDo == null) {
            return null;
        }
        sceneDetailDto = sceneDetailConvertor.DoToDto(sceneDetailDo);
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        sceneDetailDto.setStepNum(sceneStepRelDos.size());
        // 获取场景关联类目
        CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(sceneId);
        sceneDetailDto.setCategoryId(categorySceneDo.getCategoryId());
        log.info("[SceneDomainImpl:getSceneById] get scene, scene = {}", JSON.toJSONString(sceneDetailDto));
        // 更新缓存
        sceneDetailCache.updateSceneDetail(sceneId, sceneDetailDto);
        return sceneDetailDto;
    }

    private List<SceneDetailDto> batchQuerySceneByIds(List<Long> sceneIds) {
        log.info("[SceneDomainImpl:batchQuerySceneByIds] param = {}", JSON.toJSONString(sceneIds));
        List<SceneDetailDto> sceneDetailDtos = new ArrayList<>();
        for (Long sceneId : sceneIds) {
            SceneDetailDto sceneDetailDto = this.getSceneById(sceneId);
            sceneDetailDtos.add(sceneDetailDto);
        }
        log.info("[SceneDomainImpl:batchQuerySceneByIds] sceneIds = {}, result = {}",
                sceneIds, JSON.toJSONString(sceneDetailDtos));
        return sceneDetailDtos;
    }

    @Override
    public Boolean deleteScene(Long sceneId) {
        if (this.getSceneById(sceneId) == null) {
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
        // TODO: 2023/3/1 根据id搜索
        // 搜索优先级
        if ((sceneQry.getSceneName() == null || sceneQry.getSceneName() == "")
                && sceneQry.getCategoryId() != null) { // 根据类目搜索，搜索当前目录下的所有场景
            List<CategorySceneDo> categorySceneDos = categorySceneRepository.queryByCategoryId(
                    sceneQry.getCategoryId(), sceneQry.getPageQry());
            sceneIds = categorySceneDos.stream().map(categorySceneDo -> categorySceneDo.getSceneId())
                    .collect(Collectors.toList());
            log.info("[SceneDomainImpl:searchScene] by categoryId = {}, sceneIds = {}", sceneQry.getCategoryId(), sceneIds);
        } else if (sceneQry.getSceneName() != null) {  // 根据名称搜索
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
