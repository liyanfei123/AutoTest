package com.testframe.autotest.core.enums;

/**
 * 场景步骤是否执行
 */
public enum StepStatusEnum {

    OPEN(1, "开启"),
    CLOSE(2, "关闭");

    private int type;

    private String name;

    StepStatusEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static StepStatusEnum getByType(int type) {
        for (StepStatusEnum stepStatusEnum : values()) {
            if (stepStatusEnum.getType() == type) {
                return stepStatusEnum;
            }
        }
        return null;
    }

}
