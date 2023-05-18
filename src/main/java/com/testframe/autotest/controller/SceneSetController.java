package com.testframe.autotest.controller;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.command.SceneSetRelCmd;
import com.testframe.autotest.meta.command.SceneSetRelDelCmd;
import com.testframe.autotest.meta.command.SceneSetRelTopCmd;
import com.testframe.autotest.meta.vo.SetRelListVo;
import com.testframe.autotest.service.impl.SceneSetServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


// TODO: 2023/5/18 由于有全局异常捕获输出，因此可以减少try/catch使用
/**
 * 执行集
 * 执行集执行时根据绑定的createTime顺序来执行
 */
@Slf4j
@RestController
@RequestMapping("/autotest/set")
public class SceneSetController {

    @Autowired
    private SceneSetServiceImpl sceneSetService;

    @PostMapping("/update")
    public HttpResult<Long> updateSet(@RequestBody ExeSetUpdateCmd exeSetUpdateCmd) {
        try {
            return HttpResult.ok(sceneSetService.updateSceneSet(exeSetUpdateCmd));
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @GetMapping("/delete")
    public HttpResult<Long> deleteSet(@RequestParam("setId") Long setId) {
        try {
            return HttpResult.ok(sceneSetService.deleteSceneSet(setId));
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    /**
     * 保存场景/步骤与执行集的关联关系
     * 此步骤不支持置顶操作
     * @param sceneSetRelCmd
     * @return
     */
    @PostMapping("/rel/update")
    public HttpResult<Boolean> updateSetRel(@RequestBody SceneSetRelCmd sceneSetRelCmd) {
        try {
            return HttpResult.ok(sceneSetService.updateSceneSetRel(sceneSetRelCmd));
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    /**
     * 置顶/取消置顶执行集中的场景或步骤
     * @param sceneSetRelTopCmd
     * @return
     */
    @PostMapping("/rel/top")
    public HttpResult<Boolean> topSceneOrStepInSet(@RequestBody SceneSetRelTopCmd sceneSetRelTopCmd) {
        try {
            return HttpResult.ok(sceneSetService.topSetSceneOrStepRel(sceneSetRelTopCmd));
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    @PostMapping("/rel/del")
    public HttpResult<Boolean> delSceneOrStepInSet(@RequestBody SceneSetRelDelCmd sceneSetRelDelCmd) {
        try {
            return HttpResult.ok(sceneSetService.deleteSceneSetRel(sceneSetRelDelCmd));
        } catch (Exception e) {
            return HttpResult.error(e.getMessage());
        }
    }

    /**
     *
     * @param setId
     * @param page
     * @param size
     * @param status -1表示搜索所有状态数据
     * @return
     */
    @GetMapping("/query/rels")
    public HttpResult<SetRelListVo> querySceneSetScene(@RequestParam(value = "setId", required = true) Long setId,
                                                       @RequestParam(value = "type", required = true, defaultValue = "1")
                                                         Integer type,
                                                       @RequestParam(value = "page", required = true) Integer page,
                                                       @RequestParam(value = "size", required = true) Integer size,
                                                       @RequestParam(value = "status", defaultValue = "-1") Integer status) {

        if (page <= 0 || size <= 0) {
            throw new AutoTestException(500, "参数非法");
        }
        SetRelListVo setRelListVo = sceneSetService.querySetScenes(setId, status, type, page, size);
        return HttpResult.ok(setRelListVo);
    }


    @GetMapping("/test/status")
    public HttpResult<Boolean> test() {
        return HttpResult.error(2000, "自定义测试");
    }

}
