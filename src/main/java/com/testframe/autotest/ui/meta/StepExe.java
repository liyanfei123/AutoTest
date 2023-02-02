package com.testframe.autotest.ui.meta;

import lombok.Data;

// 步骤执行时的相关信息
@Data
public class StepExe {

    private Long stepId;

    private Long stepSceneId = 0L;

    private String stepName;

    private Integer operaType;

    // 是否运行
    private Integer status;

    // 定位方式
    private LocatorInfo locatorInfo;

    // 操作方式
    private OperateData operateData;

    // 用于验证的字段
    private AssertData checkData;

    // 等待方式
    private WaitInfo waitInfo;

}
