package com.testframe.autotest.core.enums;

public enum StepTypeEnum {

    STEP(1, "单步骤"),
    SCENE(2, "子场景");

    private int type;

    private String name;

    StepTypeEnum(int type, String name) {
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
