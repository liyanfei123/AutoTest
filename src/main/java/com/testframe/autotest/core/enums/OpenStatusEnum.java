package com.testframe.autotest.core.enums;

/**
 * 场景步骤是否执行
 */
public enum OpenStatusEnum {

    CLOSE(0, "关闭"),
    OPEN(1, "开启");

    private int type;

    private String name;

    OpenStatusEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static OpenStatusEnum getByType(int type) {
        for (OpenStatusEnum openStatusEnum : values()) {
            if (openStatusEnum.getType() == type) {
                return openStatusEnum;
            }
        }
        return null;
    }

}
