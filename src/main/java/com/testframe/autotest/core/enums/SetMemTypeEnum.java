package com.testframe.autotest.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合元素类型
 */
public enum SetMemTypeEnum {

    SCENE(0, "场景"),
    STEP(1, "单步骤");

    private int type;

    private String desc;

    SetMemTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static SetMemTypeEnum getByType(int type) {
        for (SetMemTypeEnum setMemTypeEnum : values()) {
            if (setMemTypeEnum.getType() == type) {
                return setMemTypeEnum;
            }
        }
        return null;
    }

    public List<Integer> getTypes() {
        List<Integer> allTypes = new ArrayList<>();
        for (SetMemTypeEnum setMemTypeEnum : values()) {
            allTypes.add(setMemTypeEnum.type);
        }
        return allTypes;
    }

}
