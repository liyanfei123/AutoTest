package com.testframe.autotest.ui.enums.check;

public enum VerifyTypeEnum {

    KEYWORD(1, "源码关键字检验", "assetPageSource");

    private int type;

    private String name;

    private String func;

    VerifyTypeEnum(int type, String name, String func) {
        this.type = type;
        this.name = name;
        this.func = func;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFunc() {
        return func;
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
