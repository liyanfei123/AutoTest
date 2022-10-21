package com.testframe.autotest.controller;

import com.testframe.autotest.command.SceneCreateCmd;
import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.core.enums.SceneTypeEnum;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.service.impl.SceneDetailImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/autotest/scene")
public class SceneController {

    private static final Logger logger = LoggerFactory.getLogger(SceneController.class);

    @Autowired
    private SceneDetailImpl sceneDetail;

    // 创建场景
    @PostMapping("/create")
    public HttpResult<Long> createScene(@RequestBody SceneCreateCmd sceneCreateCmd) {
        sceneCreateCmd.setType(SceneTypeEnum.UI.getType());
        Long sceneId = sceneDetail.create(sceneCreateCmd);
        if (sceneId == null) {
            return HttpResult.error("场景创建失败");
        }
        return HttpResult.ok(sceneId);
    }

    // 更新场景
    // 需要携带步骤信息一起保存
    @PostMapping("/update")
    public HttpResult<Long> createScene(@RequestBody SceneUpdateCmd sceneUpdateCmd) {
        sceneUpdateCmd.setType(SceneTypeEnum.UI.getType());
        sceneDetail.update(sceneUpdateCmd);
        return HttpResult.ok();
    }

    @GetMapping("/list")
    public HttpResult<Object> sceneList(@RequestParam(required = false) Long sceneId) {
        // 获取场景列表时，需注意相关数据不存在的情况
        // 比如场景数为0，最近执行状态为空
        return null;
    }

    // 执行场景
    @GetMapping("/execute")
    public HttpResult<Object> executeScene(@RequestParam(required = true) Long sceneId) {
        return null;
    }
}
