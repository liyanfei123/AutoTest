package com.testframe.autotest.ui.enums;

public enum BrowserEnum {

    CHROME(1, "谷歌浏览器"),
    FIREF0X(2, "火狐浏览器");

    private int type;

    private String browserName;

    BrowserEnum(int type, String browserName) {
        this.type = type;
        this.browserName = browserName;
    }

    public int getType() {
        return type;
    }

    public String getBrowserName() {
        return browserName;
    }

    public static BrowserEnum getByType(int type) {
        for (BrowserEnum browserEnum : values()) {
            if (browserEnum.getType() == type) {
                return browserEnum;
            }
        }
        return null;
    }

}
