package com.testframe.autotest.ui.meta;

import lombok.Data;

// 步骤执行时的相关信息
@Data
public class StepExeInfo {

    private LocatorInfo locatorInfo;

    private OperateData operateData;

}
