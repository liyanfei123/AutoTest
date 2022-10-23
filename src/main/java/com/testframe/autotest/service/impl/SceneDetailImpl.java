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
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private StepDetailImpl stepDetail;

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
            // 查询当前场景下关联的步骤
            List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            // 把已有的进行更新，新增的内容单独进行新增
            for (SceneStepRel sceneStepRel : sceneStepRels) {
                log.info("[SceneDetailImpl:update] update step in sceneId-stepId {}-{}, sceneStepRel = {}", sceneId, sceneStepRel.getStepId(), JSON.toJSONString(sceneStepRel));
                if (sceneStepRel.getIsDelete() > 0) {
                    continue;
                }
                Step step = steps.get(sceneStepRel.getStepId());
                stepDetailRepository.update(step);
                sceneStepRel.setStatus(step.getStatus());
                sceneStepRepository.updateSceneStep(sceneStepRel);
                steps.remove(sceneStepRel.getStepId());
            }
            if (!steps.isEmpty()) {
                // 还有内容没有被操作过，代表是新增的步骤
                steps.forEach((stepId, step) -> {
                    log.info("[SceneDetailImpl:update] add step in sceneId {}, step = {}", sceneId, JSON.toJSONString(step));
                    stepDetailRepository.saveStep(step);
                    sceneStepRepository.saveSceneStep(SceneStepRel.build(sceneId, step));
                });
            }
            List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
            sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.BEFORE.getType());
            SceneStepOrder sceneStepOrder;
            Integer len = sceneStepOrders.size();
            switch (len) {
                case 0:
                    // 新增
                    sceneStepOrder = SceneStepOrder.build(sceneId, stepIds.toString());
                    stepOrderRepository.saveSceneStepOrder(sceneStepOrder);
                    break;
                case 1:
                    // 更新
                    sceneStepOrder = sceneStepOrders.get(0);
                    sceneStepOrder.setOrderList(stepIds.toString());
                    stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
                    break;
                default:
                    throw new AutoTestException("当前执行顺序表存在脏数据");
            }
        } catch (AutoTestException e) {
            log.error("[SceneDetailImpl:update] update scene error, reason: {}", e.getMessage());
            return false;
        }
        return true;
    }


    @Override
    public Long save() {
        return null;
    }

    @Override
    public Long batchStepSave() {
        return null;
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
