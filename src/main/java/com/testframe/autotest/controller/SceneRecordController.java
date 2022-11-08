package com.testframe.autotest.controller;

import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import com.testframe.autotest.service.impl.SceneRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @date:2022/11/04 22:35
 * @author: lyf
 */
@Slf4j
@RestController
@RequestMapping("/autotest/scene/record")
public class SceneRecordController {

    @Autowired
    private SceneRecordServiceImpl sceneRecordService;

    @RequestMapping("/query")
    public HttpResult<SceneRecordListVo> querySceneRecords(@RequestParam(required = true) Long sceneId) {
        try {
            SceneRecordListVo sceneRecordListVo = sceneRecordService.records(sceneId);
            return HttpResult.ok(sceneRecordListVo);
        } catch (Exception e) {
            return HttpResult.ok(e.getMessage());
        }
    }
}
