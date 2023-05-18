package com.testframe.autotest.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行顺序
 * 根据updateTime排序
 */
public enum ExeOrderEnum {

    LAST(-1, "最后执行"),
    NORMAL(0, "正常执行"),
    HEAD(1, "优先执行");

    private int type;

    private String name;

    ExeOrderEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static ExeOrderEnum getByType(int type) {
        for (ExeOrderEnum exeOrderEnum : values()) {
            if (exeOrderEnum.getType() == type) {
                return exeOrderEnum;
            }
        }
        return null;
    }

    public static List<Integer> getTypes() {
        List<Integer> allTypes = new ArrayList<>();
        for (ExeOrderEnum exeOrderEnum : values()) {
            allTypes.add(exeOrderEnum.type);
        }
        return allTypes;
    }

}
