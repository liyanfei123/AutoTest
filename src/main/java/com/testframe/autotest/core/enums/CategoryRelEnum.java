package com.testframe.autotest.core.enums;

public enum CategoryRelEnum {

    STEP(1,"步骤"),
    SCENE(2, "场景"),
    SET(3, "执行集");

    private int type;

    private String name;

    CategoryRelEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static CategoryRelEnum getByType(int type) {
        for (CategoryRelEnum categoryRelEnum : values()) {
            if (categoryRelEnum.getType() == type) {
                return categoryRelEnum;
            }
        }
        return null;
    }
}
