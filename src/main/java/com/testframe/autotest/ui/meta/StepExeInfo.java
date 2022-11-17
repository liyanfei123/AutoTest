package com.testframe.autotest.ui.meta;

import com.testframe.autotest.ui.elements.module.check.CheckData;
import lombok.Data;

// 步骤执行时的相关信息
@Data
public class StepExeInfo {

    private Long stepId;

    private String stepName;

    private Integer operaType;

    private LocatorInfo locatorInfo;

    private OperateData operateData;

    // 用于验证的字段
    private CheckData checkData;

}
