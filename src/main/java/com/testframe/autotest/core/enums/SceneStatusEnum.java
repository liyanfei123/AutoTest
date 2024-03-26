package com.testframe.autotest.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景执行状态
 */
public enum SceneStatusEnum {

    INT(0, "初始化中"),
    NEVER(1, "从未执行"),
    ING(2, "执行中"),
    SUCCESS(3, "执行成功"),
    FAIL(4, "执行失败"),
    STOP(5, "手动终止"),
    INTFAIL(6, "初始化失败"),
    NONE(7, "未执行"); // 即未开启，执行集中需要使用的状态

    private int type;

    private String desc;

    SceneStatusEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static List<Integer> getTypes() {
        List<Integer> allTypes = new ArrayList<>();
        for (SceneStatusEnum sceneStatusEnum : values()) {
            allTypes.add(sceneStatusEnum.type);
        }
        return allTypes;
    }

    public static SceneStatusEnum getByType(int type) {
        for (SceneStatusEnum sceneStatusEnum : values()) {
            if (sceneStatusEnum.getType() == type) {
                return sceneStatusEnum;
            }
        }
        return null;
    }

    public static Integer sceneStatusToStepStatus(Integer sceneStatus) {
        if (sceneStatus == SceneStatusEnum.NEVER.getType()) {
            return StepRunResultEnum.NORUN.getType();
        } else if (sceneStatus == SceneStatusEnum.ING.getType()
                || sceneStatus == SceneStatusEnum.INT.getType()) {
            return StepRunResultEnum.RUN.getType();
        } else if (sceneStatus == SceneStatusEnum.FAIL.getType()
                || sceneStatus == SceneStatusEnum.INTFAIL.getType()) {
            return StepRunResultEnum.FAIL.getType();
        } else if (sceneStatus == SceneStatusEnum.STOP.getType()) {
            return StepRunResultEnum.CLOSE.getType();
        }
        return StepRunResultEnum.SUCCESS.getType();
    }

}
