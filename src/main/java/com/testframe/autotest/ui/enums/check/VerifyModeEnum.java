package com.testframe.autotest.ui.enums.check;

import java.util.Arrays;
import java.util.List;

public enum VerifyModeEnum {

    // 页面验证
    PAGE_TITLE(1, "页面标题验证", "assertPageTitle"),
    PAGE_SOURCE(1, "源码关键字检验", "assetPageSource"),
    // 元素验证
    ELEMENT_TEXT(1, "检验元素文本是否复合预期", "checkElementText"),
    ELEMENT_ATTR(1, "检查元素属性", "checkElementAttr");

    private int type;

    private String desc;

    private String func;

    VerifyModeEnum(int type, String desc, String func) {
        this.type = type;
        this.desc = desc;
        this.func = func;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getFunc() {
        return func;
    }

    public static List<VerifyModeEnum> page() {
        return Arrays.asList(PAGE_TITLE, PAGE_SOURCE);
    }

    public static List<VerifyModeEnum> element() {
        return Arrays.asList(ELEMENT_TEXT, ELEMENT_ATTR);
    }

    public static VerifyModeEnum getByType(int type) {
        for (VerifyModeEnum verifyModeEnum : values()) {
            if (verifyModeEnum.getType() == type) {
                return verifyModeEnum;
            }
        }
        return null;
    }



}
