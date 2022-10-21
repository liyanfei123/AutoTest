package com.testframe.autotest.ui.enums.operate;

import java.util.Arrays;
import java.util.List;

public enum ModuleOperateEnum {

    KEYBOARD_OPERATE(ElementOperateEnum.KEYBOADR_OPERATE,
            Arrays.asList(OperateEnum.MOUSE_LEFT, OperateEnum.MOUSE_RIGHT));


    // 元素操作大类
    private ElementOperateEnum elementOperateEnum;

    // 元素操作子类
    private List<OperateEnum> operateEnums;

    ModuleOperateEnum(ElementOperateEnum elementOperateEnum, List<OperateEnum> operateEnums) {
        this.elementOperateEnum = elementOperateEnum;
        this.operateEnums = operateEnums;
    }

}
