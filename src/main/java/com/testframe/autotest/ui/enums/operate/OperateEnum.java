package com.testframe.autotest.ui.enums.operate;

import com.testframe.autotest.core.enums.SceneTypeEnum;

public enum OperateEnum {

    MOUSE_LEFT(1, "左键"),
    MOUSE_RIGHT(2, "右键");

    private int type;

    private String desc;

    OperateEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static OperateEnum getByType(int type) {
        for (OperateEnum operateEnum : values()) {
            if (operateEnum.getType() == type) {
                return operateEnum;
            }
        }
        return null;
    }
}
