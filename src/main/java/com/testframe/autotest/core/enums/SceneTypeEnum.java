package com.testframe.autotest.core.enums;

import java.util.ArrayList;
import java.util.List;

public enum SceneTypeEnum {

    UI(1, "UI场景测试"),
    HTTP(2, "接口场景测试");

    private int type;

    private String desc;

    SceneTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static SceneTypeEnum getByType(int type) {
        for (SceneTypeEnum sceneTypeEnum : values()) {
            if (sceneTypeEnum.getType() == type) {
                return sceneTypeEnum;
            }
        }
        return null;
    }

    public List<Integer> getTypes() {
        List<Integer> allTypes = new ArrayList<>();
        for (SceneTypeEnum sceneTypeEnum : values()) {
            allTypes.add(sceneTypeEnum.type);
        }
        return allTypes;
    }

}
