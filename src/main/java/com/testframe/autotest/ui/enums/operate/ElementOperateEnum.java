package com.testframe.autotest.ui.enums.operate;

public enum ElementOperateEnum {

    KEYBOADR_OPERATE("keyboardOperate", "键盘操作"),
    MOUSE_OPERATE("mouseOperate", "鼠标操作");

    private String name;

    private String desc;

    ElementOperateEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
