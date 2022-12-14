package com.testframe.autotest.controller;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.dto.SceneDetailInfo;
import com.testframe.autotest.meta.query.SceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.service.SceneDetailService;
import com.testframe.autotest.service.SceneExecuteService;
import com.testframe.autotest.service.SceneListService;
import com.testframe.autotest.service.impl.CopyServiceImpl;
import com.testframe.autotest.util.StringUtils;
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
    private SceneDetailService sceneDetailService;

    @Autowired
    private SceneListService sceneListService;

    @Autowired
    private CopyServiceImpl sceneCopyService;

    @Autowired
    private SceneExecuteService sceneExecuteService;

    // 创建场景
    @PostMapping("/create")
    public HttpResult<Long> createScene(@RequestBody SceneCreateCmd sceneCreateCmd) {
        try {
            Long sceneId = sceneDetailService.create(sceneCreateCmd);
            return HttpResult.ok(sceneId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public HttpResult<Long> createScene(@RequestBody SceneUpdateCmd sceneUpdateCmd) {
        try {
            sceneDetailService.update(sceneUpdateCmd);
            return HttpResult.ok("场景更新成功");
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @GetMapping("query")
    public HttpResult<SceneDetailInfo> queryScene(@RequestParam(required = true) Long sceneId) {
        try {
            return HttpResult.ok(sceneDetailService.query(sceneId));
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @PostMapping("/list")
    public HttpResult<SceneListVO> sceneList(@RequestBody SceneQry sceneQry) {
        try {
            SceneListVO sceneListVO = sceneListService.queryScenes(sceneQry);
            return  HttpResult.ok(sceneListVO);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }

    }

    @GetMapping("/delete")
    public HttpResult<Object> deleteScene(@RequestParam(required = true) Long sceneId) {
        try {
            if (sceneId == null || !StringUtils.isNumeric(sceneId.toString())) {
                return HttpResult.error("请输入正确的场景id");
            }
            sceneListService.deleteScene(sceneId);
            return HttpResult.ok("删除成功");
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @GetMapping("/copy")
    public HttpResult<Object> copyScene(@RequestParam(required = true) Long sceneId) {
        try {
            if (sceneId == null) {
                return HttpResult.error("请输入场景id");
            }
            Long newSceneId = sceneCopyService.sceneCopy(sceneId);
            return HttpResult.ok(newSceneId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 执行场景
    // 没有步骤的场景不允许执行
    @GetMapping("/execute")
    public HttpResult<Object> executeScene(@RequestParam(required = true) Long sceneId) {
        try {
            sceneExecuteService.execute(sceneId);
            return HttpResult.ok(sceneId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

}
