package com.testframe.autotest.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景执行状态
 */
public enum SceneStatusEnum {

    NEVER(1, "从未执行"),
    ING(2, "执行中"),
    SUCCESS(3, "执行成功"),
    FAIL(4, "执行失败");

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

}
