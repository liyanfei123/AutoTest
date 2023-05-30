package com.testframe.autotest.controller;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.command.SceneSetRelCmd;
import com.testframe.autotest.meta.command.SceneSetRelDelCmd;
import com.testframe.autotest.meta.command.SceneSetRelTopCmd;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.vo.SetRelListVo;
import com.testframe.autotest.service.impl.SceneSetServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping("/query")
    public HttpResult<ExeSetDto> querySet(@RequestParam("setId") Long setId) {
        return HttpResult.ok(sceneSetService.querySet(setId));
    }

    // TODO: 2023/5/19 添加执行集列表查询，关联类目功能

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
            e.printStackTrace();
            return HttpResult.error(e.getMessage());
        }
    }

    /**
     * 查询步骤所关联的所有执行集
     * @param stepId
     * @return
     */
    @GetMapping("/rel/step")
    public HttpResult<List<ExeSetDto>> stepRelSet(@RequestParam("stepId") Long stepId) {
        if (stepId == null || stepId < 0) {
            throw new AutoTestException("请输入正确的参数");
        }
        return HttpResult.ok(sceneSetService.queryRelByStepId(stepId, 0L));
    }

    /**
     * 查询场景所关联的所有执行集
     * @param sceneId
     * @return
     */
    @GetMapping("/rel/scene")
    public HttpResult<List<ExeSetDto>> sceneRelSet(@RequestParam("sceneId") Long sceneId) {
        if (sceneId == null || sceneId < 0) {
            throw new AutoTestException("请输入正确的参数");
        }
        return HttpResult.ok(sceneSetService.queryRelByStepId(0L, sceneId));
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
        SetRelListVo setRelListVo = sceneSetService.querySetRels(setId, status, type, page, size);
        return HttpResult.ok(setRelListVo);
    }

    /**
     * 移动执行集所在目录
     * @param setId
     * @param oldCategoryId
     * @param newCategoryId
     * @return
     */
    @GetMapping("/category/move")
    public HttpResult<Boolean> moveCategory(@RequestParam(name = "setId", required = true) Long setId,
                                            @RequestParam(name = "oldCategoryId",required = true) Integer oldCategoryId,
                                            @RequestParam(name = "newCategoryId",required = true) Integer newCategoryId) {
        Boolean res = sceneSetService.moveCategoryId(setId, oldCategoryId, newCategoryId);
        return HttpResult.ok(res);
    }


}
