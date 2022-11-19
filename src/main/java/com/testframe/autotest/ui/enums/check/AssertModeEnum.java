package com.testframe.autotest.ui.enums.check;

import java.util.Arrays;
import java.util.List;

public enum AssertModeEnum {

    // 页面验证
    PAGE_TITLE(10, "页面标题验证", "assertPageTitle"),
    PAGE_SOURCE(11, "源码关键字检验", "assetPageSource"),
    // 元素验证
    ELEMENT_TEXT(20, "检验元素文本是否复合预期", "checkElementText"),
    ELEMENT_ATTR(21, "检查元素属性", "checkElementAttr");

    private int type;

    private String name;

    private String func;

    AssertModeEnum(int type, String name, String func) {
        this.type = type;
        this.name = name;
        this.func = func;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFunc() {
        return func;
    }

    public static List<AssertModeEnum> page() {
        return Arrays.asList(PAGE_TITLE, PAGE_SOURCE);
    }

    public static List<AssertModeEnum> element() {
        return Arrays.asList(ELEMENT_TEXT, ELEMENT_ATTR);
    }

    public static AssertModeEnum getByType(int type) {
        for (AssertModeEnum assertModeEnum : values()) {
            if (assertModeEnum.getType() == type) {
                return assertModeEnum;
            }
        }
        return null;
    }



}
