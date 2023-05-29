package com.testframe.autotest.service.impl;

import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.*;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.domain.step.StepDomain;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.dto.step.StepsDto;
import com.testframe.autotest.meta.validator.CategoryValidator;
import com.testframe.autotest.meta.vo.SceneDetailVo;
import com.testframe.autotest.meta.vo.StepInfoVo;
import com.testframe.autotest.service.SceneDetailService;
import com.testframe.autotest.meta.validator.SceneValidator;
import com.testframe.autotest.meta.validator.StepValidator;
import com.testframe.autotest.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SceneDetailImpl implements SceneDetailService {

    @Autowired
    private SceneValidator sceneValidator;

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private CategoryValidator categoryValidator;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;
    
    @Autowired
    private SceneCacheService sceneCacheService;

    @Autowired
    private SceneDomain sceneDomain;

    @Autowired
    private StepDomain stepDomain;

    @Autowired
    private CategorySceneDomain categorySceneDomain;

    // 创建测试场景
    @Override
    public Long create(SceneCreateCmd sceneCreateCmd) {
        log.info("[SceneDetailImpl:create] create scene, sceneCreateCmd = {}", JSON.toJSONString(sceneCreateCmd));
        try {
            sceneValidator.validateCreate(sceneCreateCmd);
            SceneDetailDto sceneDetailDto = this.build(sceneCreateCmd);
            // todo:获取当前登录的用户信息
            sceneDetailDto.setCreateBy(1234L);
            log.info("[SceneDetailImpl:create] create scene, scene = {}", JSON.toJSONString(sceneDetailDto));
            return sceneDomain.updateScene(sceneDetailDto);
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:create] create scene error, reason: {}", e.getMessage());
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public Boolean update(SceneUpdateCmd sceneUpdateCmd) {
        log.info("[SceneDetailImpl:update] update scene, sceneUpdateCmd = {}", JSON.toJSONString(sceneUpdateCmd));
        try {
            SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(sceneUpdateCmd.getId());
            if (sceneDetailDto == null) {
                throw new AutoTestException("请输入正确的场景id");
            }
            // null值处理
            if (sceneUpdateCmd.getCategoryId() == null) {
                sceneUpdateCmd.setCategoryId(sceneDetailDto.getCategoryId());
            }
            sceneValidator.validateUpdate(sceneUpdateCmd);
            sceneDetailDto = this.buildUpdate(sceneDetailDto, sceneUpdateCmd);
            if (sceneDetailDto == null) {
                return true;
            }
            return sceneDomain.updateScene(sceneDetailDto) == 0L;
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:update] update scene error, reason: ", e);
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public SceneDetailVo query(Long sceneId) {
        try {
            log.info("[SceneDetailImpl:query] query scene {}", sceneId);
            SceneDetailVo sceneDetailVo = new SceneDetailVo();
            SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(sceneId);
            if (sceneDetailDto == null) {
                throw new AutoTestException("当前场景不存在");
            }
            sceneDetailVo.setSceneInfo(sceneDetailDto);
            List<StepInfoVo> steps = this.buildStepInfo(sceneId);
            sceneDetailVo.setSteps(steps);
            log.info("[SceneDetailImpl:query] scene detail {}", JSON.toJSONString(sceneDetailVo));
            return sceneDetailVo;
        } catch (Exception e) {
            log.error("[SceneDetailImpl:query] query scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    private List<StepInfoVo> buildStepInfo(Long sceneId) {
        List<StepInfoVo> stepInfoVos = new ArrayList<>();
        List<StepDetailDto> stepDetailDtos = stepDomain.listStepInfo(sceneId);
        if (stepDetailDtos.isEmpty()) {
            return stepInfoVos;
        }
        for (StepDetailDto stepDetailDto : stepDetailDtos) {
            StepInfoVo stepInfoVo = new StepInfoVo();
            BeanUtils.copyProperties(stepDetailDto, stepInfoVo);
            if (stepDetailDto.getType() == StepTypeEnum.STEP.getType()) {
                // 单步骤
                stepInfoVo.setSonSteps(Collections.EMPTY_LIST);
            } else if (stepDetailDto.getType() == StepTypeEnum.SCENE.getType()) {
                // 子场景
                List<StepInfoVo> sonStepInfoVos = this.buildStepInfo(stepDetailDto.getSonSceneId());
                stepInfoVo.setSonSteps(sonStepInfoVos);
            }
            stepInfoVos.add(stepInfoVo);
        }
        return stepInfoVos;
    }

    @Override
    public Long sceneCopy(Long sceneId) {
        log.info("[SceneDetailImpl:sceneCopy] copy scene, sceneId = {}", sceneId);
        try {
            // 复制场景详情
            SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(sceneId);
            if (sceneDetailDto == null) {
                return 0L;
            }
            sceneDetailDto.setSceneId(null);
            // TODO: 2023/3/30 获取当前用户信息 
            sceneDetailDto.setCreateBy(123456L);
            String sceneSuffix = RandomUtil.randomCode(8);
            sceneDetailDto.setSceneName(sceneDetailDto.getSceneName() + sceneSuffix);
            log.info("[SceneDetailImpl:sceneCopy] copy scene, copy scene = {}", JSON.toJSONString(sceneDetailDto));
            Long newSceneId = sceneDomain.updateScene(sceneDetailDto);

            // 复制场景下的所有步骤
            log.info("[SceneDetailImpl:sceneCopy] start copy scene's steps");
            List<StepDetailDto> stepDetailDtos = stepDomain.listStepInfo(sceneId);
            if (stepDetailDtos.isEmpty()) {
                return newSceneId;
            }
            stepDetailDtos = stepDetailDtos.stream().map(stepDetailDto -> {
                stepDetailDto.setStepId(null);
                stepDetailDto.setSceneId(newSceneId);
                return stepDetailDto;
            }).collect(Collectors.toList());
            StepsDto stepsDto = new StepsDto();
            stepsDto.setSceneId(sceneId);
            stepsDto.setStepDetailDtos(stepDetailDtos);
            stepDomain.saveSteps(stepsDto);
            return newSceneId;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[SceneDetailImpl:sceneCopy] copy scene has error, reason = {}", e);
            throw new AutoTestException("场景复制失败");
        }
    }

    @Override
    public Boolean deleteScene(Long sceneId) {
        return sceneDomain.deleteScene(sceneId);
    }

    @Override
    public List<Long> moveScene(List<Long> sceneIds, Integer oldCategoryId, Integer newCategoryId) {
        log.info("[SceneDetailImpl:moveScene] move scenes {} from {} to categoryId {}", JSON.toJSONString(sceneIds),
                oldCategoryId, newCategoryId);
        if (oldCategoryId == newCategoryId || sceneIds.isEmpty() || sceneIds == null) {
            return Collections.EMPTY_LIST;
        }
        categoryValidator.checkCategoryId(newCategoryId);
        categoryValidator.checkCategoryId(oldCategoryId);
        List<SceneDetailDto> sceneDetailDtos = sceneValidator.sceneIsExistInCategoryId(sceneIds, oldCategoryId);
        List<CategorySceneDto> categorySceneDtos = new ArrayList<>();
        List<Long> repeatSceneIds = new ArrayList<>();
        for (SceneDetailDto sceneDetailDto : sceneDetailDtos) {
            // 标题重复的会自动过滤，不进行迁移
            try {
                sceneValidator.checkSceneTitle(sceneDetailDto.getSceneName(), newCategoryId, sceneDetailDto.getSceneId());
                CategorySceneDto categorySceneDto = new CategorySceneDto();
                categorySceneDto.setSceneId(sceneDetailDto.getSceneId());
                categorySceneDto.setCategoryId(newCategoryId);
                categorySceneDtos.add(categorySceneDto);
            } catch (Exception e) {
                log.warn("[SceneDetailImpl:moveScene] title repeat!");
                repeatSceneIds.add(sceneDetailDto.getSceneId());
            }
        }
        categorySceneDomain.batchUpdateCategoryScene(oldCategoryId, categorySceneDtos);
        return repeatSceneIds;
    }

    private SceneDetailDto build(SceneCreateCmd sceneCreateCmd) {
        SceneDetailDto sceneDetailDto = new SceneDetailDto();
        sceneDetailDto.setSceneName(sceneCreateCmd.getTitle());
        sceneDetailDto.setSceneDesc(sceneCreateCmd.getDesc());
        sceneDetailDto.setType(sceneCreateCmd.getType());
        sceneDetailDto.setCategoryId(sceneCreateCmd.getCategoryId());
        return sceneDetailDto;
    }

    // 判断场景是否需要更新
    private SceneDetailDto buildUpdate(SceneDetailDto sceneDetailDto, SceneUpdateCmd sceneUpdateCmd) {
        sceneDetailDto.setSceneId(sceneUpdateCmd.getId());
        Boolean flag = false;
        if (sceneUpdateCmd.getTitle() != null && !sceneUpdateCmd.getTitle().equals(sceneDetailDto.getSceneName())) {
            sceneDetailDto.setSceneName(sceneUpdateCmd.getTitle());
            flag = true;
        }
        if (sceneUpdateCmd.getDesc() != null && !sceneUpdateCmd.getDesc().equals(sceneDetailDto.getSceneDesc())) {
            sceneDetailDto.setSceneDesc(sceneUpdateCmd.getDesc());
            flag = true;
        }
        if (sceneUpdateCmd.getUrl() != null && !sceneUpdateCmd.getUrl().equals(sceneDetailDto.getUrl())) {
            sceneDetailDto.setUrl(sceneUpdateCmd.getUrl());
            flag = true;
        }
        if (sceneUpdateCmd.getWaitType() != null && sceneUpdateCmd.getWaitType() != sceneDetailDto.getWaitType()) {
            sceneDetailDto.setWaitType(sceneUpdateCmd.getWaitType());
            flag = true;
        }
        if (sceneUpdateCmd.getWaitTime() != null && sceneUpdateCmd.getWaitTime() != sceneDetailDto.getWaitTime()) {
            sceneDetailDto.setWaitTime(sceneUpdateCmd.getWaitTime());
            flag = true;
        }
        if (sceneUpdateCmd.getCategoryId() != null && sceneUpdateCmd.getCategoryId() != sceneDetailDto.getCategoryId()) {
            sceneDetailDto.setCategoryId(sceneUpdateCmd.getCategoryId());
            flag = true;
        }
        if (flag == true) {
            return sceneDetailDto;
        } else {
            return null;
        }
    }
}
