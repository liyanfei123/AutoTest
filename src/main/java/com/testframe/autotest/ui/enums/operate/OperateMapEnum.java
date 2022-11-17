package com.testframe.autotest.ui.enums.operate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 元素操作方式
 */
public enum OperateMapEnum {

    MOUSE_OPERATE(OperateEnum.MOUSE_OPERATE, OperateModeEnum.allTypes());

    // 元素操作大类
    private OperateEnum operateEnum;

    // 元素操作子类
    private List<OperateModeEnum> operateModeEnums;

    OperateMapEnum(OperateEnum operateEnum, List<OperateModeEnum> operateModeEnums) {
        this.operateEnum = operateEnum;
        this.operateModeEnums = operateModeEnums;
    }

}
