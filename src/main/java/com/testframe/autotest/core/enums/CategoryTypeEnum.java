package com.testframe.autotest.core.enums;

public enum CategoryTypeEnum {

    PRIMARY(1, "一级目录"),
    MULTI(2, "多级目录");

    private int type;

    private String name;

    CategoryTypeEnum(int type, String name) {
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
