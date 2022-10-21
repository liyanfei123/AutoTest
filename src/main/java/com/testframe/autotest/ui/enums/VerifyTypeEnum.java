package com.testframe.autotest.ui.enums;

public enum VerifyTypeEnum {

    KEYWORD(1, "源码关键字检验");

    private int type;

    private String name;

    VerifyTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static VerifyTypeEnum getByType(int type) {
        for (VerifyTypeEnum verifyTypeEnum : values()) {
            if (verifyTypeEnum.getType() == type) {
                return verifyTypeEnum;
            }
        }
        return null;
    }



}
