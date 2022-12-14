package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.repository.*;
import com.testframe.autotest.meta.bo.*;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.dto.SceneDetailInfo;
import com.testframe.autotest.meta.dto.SceneInfoDto;
import com.testframe.autotest.meta.dto.StepInfoDto;
import com.testframe.autotest.service.SceneDetailService;
import com.testframe.autotest.service.SceneStepService;
import com.testframe.autotest.service.StepDetailService;
import com.testframe.autotest.service.StepOrderService;
import com.testframe.autotest.meta.validator.SceneValidator;
import com.testframe.autotest.meta.validator.StepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.transaction.annotation.Transactional;

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
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepService sceneStepService;

    @Autowired
    private StepDetailService stepDetailService;

    @Autowired
    private StepOrderService stepOrderService;

    // 创建测试场景
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(SceneCreateCmd sceneCreateCmd) {
        log.info("[SceneDetailImpl:create] create scene, sceneCreateCmd = {}", JSON.toJSONString(sceneCreateCmd));
        // 检验参数是否符合要求
        try {
            sceneValidator.validateCreate(sceneCreateCmd);
            Scene sceneCreate = build(sceneCreateCmd);
            // todo:获取当前登录的用户信息
            sceneCreate.setCreateBy(1234L);
            log.info("[SceneDetailImpl:create] create scene, scene = {}", JSON.toJSONString(sceneCreate));
            return sceneDetailRepository.saveScene(sceneCreate);
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:create] create scene error, reason: {}", e.getMessage());
            throw new AutoTestException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean update(SceneUpdateCmd sceneUpdateCmd) {
        log.info("[SceneDetailImpl:update] update scene, sceneUpdateCmd = {}", JSON.toJSONString(sceneUpdateCmd));
        Long sceneId = sceneUpdateCmd.getId();
        try {
            sceneUpdateCmd.getStepUpdateCmds().forEach(stepUpdateCmd -> stepUpdateCmd.setSceneId(sceneId));
            // 更新场景概要
            sceneValidator.checkSceneUpdate(sceneUpdateCmd);
            stepValidator.checkStepUpdates(sceneUpdateCmd.getStepUpdateCmds());
            Scene sceneUpdate = build(sceneUpdateCmd);
            sceneDetailRepository.update(sceneUpdate);
            if (sceneUpdateCmd.getStepUpdateCmds().isEmpty()) {
                return true;
            }
            List<Step> steps = new ArrayList<>();
            for (StepUpdateCmd stepUpdateCmd : sceneUpdateCmd.getStepUpdateCmds()) {
                Step step = StepUpdateCmd.toStep(stepUpdateCmd);
                steps.add(step);
            }
            // 更新场景下的所有步骤
            List<Long> stepIds = sceneStepService.updateSceneStep(sceneId, steps);
            // 更新执行步骤顺序
            stepOrderService.updateStepOrder(sceneId, stepIds);
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:update] update scene error, reason: ", e);
            throw new AutoTestException(e.getMessage());
        }
        return true;
    }

    @Override
    public SceneDetailInfo query(Long sceneId) {
        try {
            log.info("[SceneDetailImpl:query] query scene {}", sceneId);
            Scene scene = sceneDetailRepository.querySceneById(sceneId);
            if (scene == null) {
                throw new AutoTestException("当前场景不存在");
            }
            SceneInfoDto sceneInfoDto = SceneInfoDto.build(scene);
            SceneDetailInfo sceneDetailInfo = new SceneDetailInfo();
            sceneDetailInfo.setScene(sceneInfoDto);
            // 查询步骤执行信息
            List<Long> stepIds = stepOrderService.queryNowStepOrder(sceneId);
            if (stepIds.isEmpty()) {
                sceneDetailInfo.setSteps(null);
            } else {
                HashMap<Long, StepInfoDto> stepInfoDtoMap = stepDetailService.batchQueryStepDetail(stepIds);
                List<StepInfoDto> steps = new ArrayList<>(stepIds.size());
                // 根据执行步骤编排信息
                stepIds.forEach(stepId -> {
                    StepInfoDto stepInfoDto = stepInfoDtoMap.get(stepId);
                    if (stepInfoDto == null) {
                        throw new AutoTestException("当前场景下步骤被删除，数据有误");
                    }
                    steps.add(stepInfoDto);
                });
                sceneDetailInfo.setSteps(steps);
            }
            log.info("[SceneDetailImpl:query] scene detail {}", JSON.toJSONString(sceneDetailInfo));
            return sceneDetailInfo;
        } catch (Exception e) {
            log.error("[SceneDetailImpl:query] query scene {} error, reason = {}", sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }

    private Scene build(SceneCreateCmd sceneCreateCmd) {
        Scene sceneCreate = new Scene();
        sceneCreate.setTitle(sceneCreateCmd.getTitle());
        sceneCreate.setDesc(sceneCreateCmd.getDesc());
        sceneCreate.setType(sceneCreateCmd.getType());
        return sceneCreate;
    }

    private Scene build(SceneUpdateCmd sceneUpdateCmd) {
        Scene sceneUpdate = new Scene();
        sceneUpdate.setId(sceneUpdateCmd.getId());
        sceneUpdate.setTitle(sceneUpdateCmd.getTitle());
        sceneUpdate.setDesc(sceneUpdateCmd.getDesc());
        sceneUpdate.setType(sceneUpdateCmd.getType());
        sceneUpdate.setUrl(sceneUpdateCmd.getUrl());
        sceneUpdate.setWaitType(sceneUpdateCmd.getWaitType());
        sceneUpdate.setWaitTime(sceneUpdateCmd.getWaitTime());
        return sceneUpdate;
    }
}
