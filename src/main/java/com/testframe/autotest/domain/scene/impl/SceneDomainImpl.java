package com.testframe.autotest.domain.scene.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.*;
import com.testframe.autotest.core.meta.convertor.SceneDetailConvertor;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.scene.SceneSearchListDto;
import com.testframe.autotest.meta.query.SceneQry;
import lombok.extern.slf4j.Slf4j;
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
    private CategorySceneRepository categorySceneRepository;
    @Autowired
    private SceneDetailConvertor sceneDetailConvertor;


    @Override
    public Long updateScene(SceneDetailDto sceneDetailDto) {
        try {
            SceneDo sceneDo = new SceneDo();
            if (sceneDetailDto.getSceneId() != null && sceneDetailDto.getSceneId() > 0) {
                // 更新场景
                SceneDetailDo sceneDetailDo = sceneDetailConvertor.DtoToDo(sceneDetailDto);
                sceneDo.setSceneDetailDo(sceneDetailDo);
                CategorySceneDo existCategorySceneDo = null;
                if (sceneDetailDto.getCategoryId() != null && sceneDetailDto.getCategoryId() > 0) {
                    log.info("[SceneDomainImpl:updateScene] update scene, {}", JSON.toJSONString(sceneDetailDto));
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
                return sceneDetailRepository.updateScene(sceneDo) ? 0L : -1L;
            } else {
                // 新增场景
                SceneDetailDo sceneDetailDo = sceneDetailConvertor.DtoToDo(sceneDetailDto);
                sceneDetailDo.setIsDelete(0);
                sceneDo.setSceneDetailDo(sceneDetailDo);
                CategorySceneDo categorySceneDo = new CategorySceneDo(null, sceneDetailDto.getCategoryId(), null, null);
                sceneDo.setCategorySceneDo(categorySceneDo);
                return sceneDetailRepository.saveSceneInit(sceneDo);
            }
        } catch (Exception e) {
            log.error("[SceneDomainImpl:updateScene] update scene error, reason: ", e);
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public SceneDetailDto getSceneById(Long sceneId) {
        SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(sceneId);
        if (sceneDetailDo == null) {
            return null;
        }
        SceneDetailDto sceneDetailDto = sceneDetailConvertor.DoToDto(sceneDetailDo);
        log.info("[SceneDomainImpl:getSceneById] get scene = {}", JSON.toJSONString(sceneDetailDto));
        List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneId);
        sceneDetailDto.setStepNum(sceneStepRelDos.size());
        // 获取场景关联类目
        CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(sceneId);
        sceneDetailDto.setCategoryId(categorySceneDo.getCategoryId());
        return sceneDetailDto;
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
        SceneSearchListDto sceneSearchListDto = new SceneSearchListDto();
        List<SceneDetailDto> sceneDetailDtos;
        List<SceneDetailDo> sceneDetailDos = new ArrayList<>();
        // TODO: 2023/3/1 根据id搜索
        // 搜索优先级
        if ((sceneQry.getSceneName() == null || sceneQry.getSceneName() == "")
                && sceneQry.getCategoryId() != null) { // 根据类目搜索，搜索当前目录下的所有场景
            List<CategorySceneDo> categorySceneDos = categorySceneRepository.queryByCategoryId(
                    sceneQry.getCategoryId(), sceneQry.getPageQry());
            if (categorySceneDos.isEmpty()) {
                return sceneSearchListDto;
            }
            List<Long> sceneIds = categorySceneDos.stream().map(categorySceneDo -> categorySceneDo.getSceneId())
                    .collect(Collectors.toList());
            sceneDetailDos = sceneDetailRepository.batchQuerySceneByIds(sceneIds);
        } else if (sceneQry.getSceneName() != null) {  // 根据名称搜索
            sceneDetailDos = sceneDetailRepository.queryScenes(null,
                    sceneQry.getSceneName(), sceneQry.getCategoryId(), sceneQry.getStatus(), sceneQry.getPageQry());
        }

        if (sceneDetailDos.isEmpty()) {
            return sceneSearchListDto;
        }

        sceneDetailDtos = sceneDetailDos.stream().map(sceneDetailConvertor::DoToDto)
                .collect(Collectors.toList());
        for (SceneDetailDto sceneDetailDto : sceneDetailDtos) {
            List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneDetailDto.getSceneId());
            sceneDetailDto.setStepNum(sceneStepRelDos.size());
        }
        sceneSearchListDto.setSceneDetailDtos(sceneDetailDtos);
        return sceneSearchListDto;
    }


}
