package com.testframe.autotest.core.enums;

public enum StepOrderEnum {

    BEFORE(1, "执行前的顺序"),
    ING(2, "执行中的顺序");

    private int type;

    private String name;

    StepOrderEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static Boolean contains(int type) {
        for (StepOrderEnum stepOrderEnum : values()) {
            if (stepOrderEnum.type == type) {
                return true;
            }
        }
        return false;
    }
}
