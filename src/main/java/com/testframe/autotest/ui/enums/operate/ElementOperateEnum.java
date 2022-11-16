package com.testframe.autotest.ui.enums.operate;

/**
 * 元素操作类型
 */
public enum ElementOperateEnum {

    KEYBOARD_OPERATE(1, "keyboardOperate", "键盘操作"),
    MOUSE_OPERATE(2,"mouseOperate", "鼠标操作"),

    JS_OPERATE(9, "jsOperate", "js操作");

    private int type;
    private String name;

    private String desc;

    ElementOperateEnum(int type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;
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

}
