package com.testframe.autotest.ui.enums;

/**
 * 操作类型
 */
public enum OperateTypeEnum {

    OPERATE(1, "元素操作"),
    WAIT(2, "元素等待"),
    ASSERT(3, "元素检验");

    private int type;

    private String name;

    OperateTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static OperateTypeEnum getOperateByType(int type) {
        for (OperateTypeEnum operateTypeEnum : values()) {
            if (operateTypeEnum.getType() == type) {
                return operateTypeEnum;
            }
        }
        return null;
    }

}
