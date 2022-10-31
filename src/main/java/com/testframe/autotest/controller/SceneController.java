package com.testframe.autotest.controller;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.core.enums.SceneTypeEnum;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.query.SceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.service.impl.CopyServiceImpl;
import com.testframe.autotest.service.impl.SceneDetailImpl;
import com.testframe.autotest.service.impl.SceneListInterImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/autotest/scene")
public class SceneController {

    private static final Logger logger = LoggerFactory.getLogger(SceneController.class);

    @Autowired
    private SceneDetailImpl sceneDetail;

    @Autowired
    private SceneListInterImpl sceneListInter;

    @Autowired
    private CopyServiceImpl sceneCopyService;

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

    @PostMapping("/update")
    public HttpResult<Long> createScene(@RequestBody SceneUpdateCmd sceneUpdateCmd) {
        sceneUpdateCmd.setType(SceneTypeEnum.UI.getType());
        if (sceneDetail.update(sceneUpdateCmd)) {
            return  HttpResult.ok();
        }
        return HttpResult.error();
    }

    @GetMapping("/list")
    public HttpResult<Object> sceneList(@RequestBody SceneQry sceneQry) {
        SceneListVO sceneListVO = sceneListInter.queryScenes(sceneQry);
        return  HttpResult.ok(sceneListVO);
    }

    @GetMapping("/delete")
    public HttpResult<Object> deleteScene(@RequestParam(required = true) Long sceneId) {
        try {
            sceneListInter.deleteScene(sceneId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
        return HttpResult.ok();
    }

    @GetMapping("/copy")
    public HttpResult<Object> copyScene(@RequestParam(required = true) Long sceneId) {
        try {
            Long newSceneId = sceneCopyService.sceneCopy(sceneId);
            return HttpResult.ok(newSceneId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 执行场景
    @GetMapping("/execute")
    public HttpResult<Object> executeScene(@RequestParam(required = true) Long sceneId) {
        return null;
    }

}
