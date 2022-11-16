package com.testframe.autotest.ui.enums.operate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 元素操作方式
 */
public enum ModuleOperateEnum {

    MOUSE_OPERATE(ElementOperateEnum.MOUSE_OPERATE, OperateEnum.allTypes());


    // 元素操作大类
    private ElementOperateEnum elementOperateEnum;

    // 元素操作子类
    private List<OperateEnum> operateEnums;

    ModuleOperateEnum(ElementOperateEnum elementOperateEnum, List<OperateEnum> operateEnums) {
        this.elementOperateEnum = elementOperateEnum;
        this.operateEnums = operateEnums;
    }

}
