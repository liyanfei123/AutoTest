package com.testframe.autotest.ui.enums.check;

import java.util.ArrayList;
import java.util.List;

/**
 * 元素验证的大类
 */
public enum AssertEnum {

    PAGE_ASSERT(1, "pageAssert","页面验证", AssertModeEnum.page()),
    ELEMENT_ASSERT(2, "elementAssert","元素验证", AssertModeEnum.element());

    private int type;

    private String name;

    private String desc;

    // 元素操作子类
    private List<AssertModeEnum> assertModeEnums;

    AssertEnum(int type, String desc, String name, List<AssertModeEnum> assertModeEnums) {
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.assertModeEnums = assertModeEnums;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<AssertModeEnum> getAssertModeEnums() {
        return assertModeEnums;
    }

    public static AssertEnum getByAssertMode(Integer mode) {
        AssertModeEnum assertModeEnum = AssertModeEnum.getByType(mode); // 最小检验类
        for (AssertEnum assertEnum : values()) {
            if (assertEnum.getAssertModeEnums().contains(assertModeEnum)) {
                return assertEnum;
            }
        }
        return null;
    }

    public static List<AssertEnum> getTypes() {
        List<AssertEnum> allTypes = new ArrayList<>();
        for (AssertEnum assertEnum : values()) {
            allTypes.add(assertEnum);
        }
        return allTypes;
    }

}
