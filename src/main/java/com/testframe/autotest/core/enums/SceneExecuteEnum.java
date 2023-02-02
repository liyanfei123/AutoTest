package com.testframe.autotest.core.enums;

// 场景执行类型
public enum SceneExecuteEnum {

    SINGLE(1, "单独执行"),
    BELOW(2, "作为子场景执行");

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

}
