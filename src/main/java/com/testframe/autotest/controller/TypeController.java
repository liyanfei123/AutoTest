package com.testframe.autotest.controller;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.dto.type.AllTypeInfo;
import com.testframe.autotest.service.impl.TypeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 *
 * @date:2022/11/19 17:50
 * @author: lyf
 */
@Slf4j
@RestController
@RequestMapping("/autotest/scene")
public class TypeController {

    @Autowired
    private TypeServiceImpl typeService;

    @GetMapping("/types")
    public HttpResult<AllTypeInfo> getTypes() {
        try {
            AllTypeInfo allTypeInfo = typeService.getAllType();
            return HttpResult.ok(allTypeInfo);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

}
