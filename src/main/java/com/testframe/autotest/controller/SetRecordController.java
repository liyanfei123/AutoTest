package com.testframe.autotest.controller;


import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.vo.SceneExeRecordVo;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import com.testframe.autotest.service.SetRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 执行集记录查询
 */
@Slf4j
@RestController
@RequestMapping("/autotest/set/record")
public class SetRecordController {

    @Autowired
    private SetRecordService setRecordService;

    @GetMapping("list")
    public HttpResult<SceneRecordListVo> listRecord(@RequestParam(required = true) Long setId) {
        List<SetExecuteRecordDto> setExecuteRecordDtos = setRecordService.records(setId);
        return HttpResult.ok(setExecuteRecordDtos);
    }

    @GetMapping("query")
    public HttpResult<SceneRecordListVo> queryByRecordId(@RequestParam(required = true) Long setRecordId) {
        List<SceneExeRecordVo> sceneExeRecordVos = setRecordService.recordsBySetId(setRecordId);
        return HttpResult.ok(sceneExeRecordVos);
    }
}
