package com.testframe.autotest.ui.enums.check;

import com.testframe.autotest.ui.enums.operate.OperateModeEnum;

import java.util.List;

public enum VerifyEnum {

    PAGE_VERIFY(1, "页面验证", VerifyModeEnum.page()),
    ELEMENT_VERIFY(2, "元素验证", VerifyModeEnum.element());

    private int type;

    private String name;

    // 元素操作子类
    private List<VerifyModeEnum> verifyModeEnums;

    VerifyEnum(int type, String name, List<VerifyModeEnum> verifyModeEnums) {
        this.type = type;
        this.name = name;
        this.verifyModeEnums = verifyModeEnums;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<VerifyModeEnum> getVerifyModeEnums() {
        return verifyModeEnums;
    }

}
