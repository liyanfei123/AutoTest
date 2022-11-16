package com.testframe.autotest.ui.enums.operate;

import java.util.ArrayList;
import java.util.List;

public enum OperateEnum {

    // type第一位不可修改
    // 键盘操作
    KEYBOARD_CTRL(1, "ctrl按键", "ctrl"),
    KEYBOARD_SHIFT(1, "shift按键", "shift"),
    KEYBOARD_ALT(1, "alt按键", "alt"),
    KEYBOARD_ENTER(1, "enter按键", "enter"),
    KEYBOARD_CTRLCV(1, "ctrlCV按键", "ctrlCV"),
    KEYBOARD_INPUT(1, "input按键", "input"),
    // 鼠标操作
    MOUSE_SINGLE_CLICK(1, "单击", "click"),
    MOUSE_DOUBLE_CLICK(2, "双击", "doubleClick"),
    MOUSE_LEFT_CLICK(3, "左键", "leftClick"),

    // frame操作
    FRAME_DEFAULTFRAME(1, "切换回默认frame", "switchDefaultFrame"),
    FRAME_FRAME_BYID(1, "根据id切换frame", "switchFrameById"),
    FRAME_FRAME_BYELEMENT(1, "根据元素切换frame", "switchFrameByElement"),

    // 单选框/复选框操作
    BOX_SELECTED(1, "选中单选框", "selected"),


    // 窗口操作
    WINOW_SWITCH(1, "切换窗口", "switchWindow");


    // 鼠标操作

    private int type;

    private String desc;

    private String func;

    OperateEnum(int type, String desc, String func) {
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

    public static List<OperateEnum> allTypes() {
        List<OperateEnum> enums = new ArrayList<>();
        for (OperateEnum operateEnum : values()) {
            enums.add(operateEnum);
        }
        return enums;
    }

    public static OperateEnum getByType(int type) {
        for (OperateEnum mouseOperateEnum : values()) {
            if (mouseOperateEnum.getType() == type) {
                return mouseOperateEnum;
            }
        }
        return null;
    }
}
