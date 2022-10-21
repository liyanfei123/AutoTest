package com.testframe.autotest.core.enums;

/**
 * 步骤运行结果
 */
public enum StepRunResultEnum {

    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    STOP(2, "暂停"),
    CLOSE(3, "终止"); // 因为失败导致未执行

    private int type;

    private String name;

    StepRunResultEnum(int type, String name) {
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
