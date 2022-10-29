package com.testframe.autotest.service.impl;

import com.testframe.autotest.command.SceneCreateCmd;
import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.command.StepUpdateCmd;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.service.SceneDetailInter;
import com.testframe.autotest.service.SceneStepInter;
import com.testframe.autotest.validator.SceneValidator;
import com.testframe.autotest.validator.StepValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Service
public class SceneDetailImpl implements SceneDetailInter {

    @Autowired
    private SceneValidator sceneValidator;

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private SceneStepInterImpl sceneStepInter;

    @Autowired
    private StepOrderImpl stepOrder;




    // 创建测试场景
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
            sceneDetailRepository.saveScene(sceneCreate);
            return sceneCreate.getId();
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:create] create scene error, reason: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean update(SceneUpdateCmd sceneUpdateCmd) {
        log.info("[SceneDetailImpl:update] update scene, sceneUpdateCmd = {}", JSON.toJSONString(sceneUpdateCmd));
        Long sceneId = sceneUpdateCmd.getId();
        try {
            sceneValidator.checkSceneUpdate(sceneUpdateCmd);
            Scene sceneUpdate = build(sceneUpdateCmd);
            HashMap<Long, Step> steps = new HashMap<>();
            List<Long> stepIds = new ArrayList<>();
            for (StepUpdateCmd stepUpdateCmd : sceneUpdateCmd.getStepUpdateCmds()) {
                stepValidator.checkStepUpdate(stepUpdateCmd);
                Step step = StepUpdateCmd.toStep(stepUpdateCmd);
                steps.put(stepUpdateCmd.getStepId(), step);
                stepIds.add(stepUpdateCmd.getStepId());
            }
            sceneDetailRepository.update(sceneUpdate);
            // 更新场景下的所有步骤
            sceneStepInter.updateSceneStep(sceneId, steps);
            // 更新执行步骤顺序
            stepOrder.updateStepOrder(sceneId, stepIds);
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:update] update scene error, reason: {}", e.getMessage());
            return false;
        }
        return true;
    }

    private List<Long> orderListStr(String stepIdsStr) {
        List<Long> stepIds = new ArrayList<>();
        String tm = stepIdsStr.substring(1, stepIdsStr.length()-1);
        String[] ts = tm.split(",");
        for (String k : ts) {
            stepIds.add(Long.parseLong(k));
        }
        return stepIds;
    }

    private Scene build(SceneCreateCmd sceneCreateCmd) {
        Scene sceneCreate = new Scene();
        sceneCreate.setTitle(sceneCreateCmd.getTitle());
        sceneCreate.setDesc(sceneCreateCmd.getDesc());
        sceneCreate.setType(sceneCreate.getType());
        return sceneCreate;
    }

    private Scene build(SceneUpdateCmd sceneUpdateCmd) {
        Scene sceneUpdate = new Scene();
        sceneUpdate.setTitle(sceneUpdateCmd.getTitle());
        sceneUpdate.setDesc(sceneUpdateCmd.getDesc());
        sceneUpdate.setType(sceneUpdateCmd.getType());
        sceneUpdate.setUrl(sceneUpdateCmd.getUrl());
        sceneUpdate.setWaitType(sceneUpdate.getWaitType());
        sceneUpdate.setWaitTime(sceneUpdate.getWaitTime());
        return sceneUpdate;
    }
}
