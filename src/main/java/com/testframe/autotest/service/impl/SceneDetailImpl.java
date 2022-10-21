package com.testframe.autotest.service.impl;

import com.testframe.autotest.command.SceneCreateCmd;
import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.service.SceneDetailInter;
import com.testframe.autotest.validator.SceneValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;


@Slf4j
@Service
public class SceneDetailImpl implements SceneDetailInter {

    @Autowired
    private SceneValidator sceneValidator;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

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
        try {
            sceneValidator.checkSceneUpdate(sceneUpdateCmd);
            Scene sceneUpdate = build(sceneUpdateCmd);
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
