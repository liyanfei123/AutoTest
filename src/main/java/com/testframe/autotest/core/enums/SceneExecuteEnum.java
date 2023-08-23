package com.testframe.autotest.core.enums;

import com.testframe.autotest.ui.enums.BrowserEnum;

// 场景执行类型
public enum SceneExecuteEnum {

    NULL(-999, "错误"),
    SINGLE(1, "单独执行"),
    BELOW(2, "作为子场景执行"),
    SET(3, "执行集执行");

    private int type;

    private String name;

    SceneExecuteEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static SceneExecuteEnum getByType(int type) {
        for (SceneExecuteEnum sceneExecuteEnum : values()) {
            if (sceneExecuteEnum.getType() == type) {
                return sceneExecuteEnum;
            }
        }
        return NULL;
    }

}
