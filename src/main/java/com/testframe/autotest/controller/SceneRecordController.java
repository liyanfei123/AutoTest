package com.testframe.autotest.controller;

import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import lombok.extern.slf4j.Slf4j;
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


    @RequestMapping("/query")
    public HttpResult<SceneRecordListVo> querySceneRecords(@RequestParam(required = true) Long sceneId) {
        return HttpResult.ok();
    }
}
