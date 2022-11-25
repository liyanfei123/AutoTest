package com.testframe.autotest.core.enums;

/**
 * 步骤运行结果
 */
public enum StepRunResultEnum {

    SUCCESS(1, "成功"),
    FAIL(2, "失败"),
    RUN(3, "执行中"),
    STOP(4, "暂停"),
    CLOSE(5, "终止"), // 因为失败导致未执行
    NORUN(6, "未执行"); // 初始状态

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
