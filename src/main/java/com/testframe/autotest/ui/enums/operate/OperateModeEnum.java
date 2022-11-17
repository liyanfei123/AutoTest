package com.testframe.autotest.ui.enums.operate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum OperateModeEnum {

    // type第一位不可修改
    // 键盘操作
    KEYBOARD_CTRL(10, "ctrl按键", "ctrl"),
    KEYBOARD_SHIFT(12, "shift按键", "shift"),
    KEYBOARD_ALT(13, "alt按键", "alt"),
    KEYBOARD_ENTER(14, "enter按键", "enter"),
    KEYBOARD_CTRLCV(15, "ctrlCV按键", "ctrlCV"),
    KEYBOARD_INPUT(16, "input按键", "input"),
    // 鼠标操作
    MOUSE_SINGLE_CLICK(20, "单击", "click"),
    MOUSE_DOUBLE_CLICK(21, "双击", "doubleClick"),
    MOUSE_LEFT_CLICK(22, "左键", "leftClick"),
    // frame操作
    FRAME_DEFAULT_FRAME(30, "切换回默认frame", "switchDefaultFrame"),
    FRAME_FRAME_BYID(31, "根据id切换frame", "switchFrameById"),
    FRAME_FRAME_BYELEMENT(32, "根据元素切换frame", "switchFrameByElement"),
    // 单选框/复选框操作
    BOX_SELECTED(40, "选中单选框", "selected"),
    BOX_NOT_SELECTED(41, "取消选中单选框", "notSelected"),
    BOX_SELECT_SINGLE_DROP(42, "单选选中下拉列表中的元素", "selectMultiDropList"),
    // JS弹窗操作
    POP_ALERT(50, "选中单选框", "selected"),
    POP_CONFIRM(51, "操作confirm弹窗", "handleConfirm"),
    POP_PROMPT(52, "操作prompt弹窗", "handlePrompt"),
    // 脚本操作
    EXECUTE_JS(60, "确认Alert弹窗", "handleAlert"),
    // 窗口操作
    WINOW_SWITCH(70, "切换窗口", "switchWindow");

    private int type;

    private String desc;

    private String func;

    OperateModeEnum(int type, String desc, String func) {
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

    public static List<OperateModeEnum> keyBoard() {
        return Arrays.asList(KEYBOARD_CTRL, KEYBOARD_SHIFT, KEYBOARD_ALT, KEYBOARD_ENTER,
                KEYBOARD_CTRLCV, KEYBOARD_INPUT);
    }

    public static List<OperateModeEnum> mouse() {
        return Arrays.asList(MOUSE_SINGLE_CLICK, MOUSE_DOUBLE_CLICK, MOUSE_LEFT_CLICK);
    }

    public static List<OperateModeEnum> frame() {
        return Arrays.asList(FRAME_DEFAULT_FRAME, FRAME_FRAME_BYID, FRAME_FRAME_BYELEMENT);
    }

    public static List<OperateModeEnum> box() {
        return Arrays.asList(BOX_SELECTED, BOX_NOT_SELECTED, BOX_SELECT_SINGLE_DROP);
    }

    public static List<OperateModeEnum> pop() {
        return Arrays.asList(POP_ALERT, POP_CONFIRM, POP_PROMPT);
    }

    public static List<OperateModeEnum> script() {
        return Arrays.asList(EXECUTE_JS);
    }

    public static List<OperateModeEnum> window() {
        return Arrays.asList(WINOW_SWITCH);
    }


    public static List<OperateModeEnum> allTypes() {
        List<OperateModeEnum> enums = new ArrayList<>();
        for (OperateModeEnum operateModeEnum : values()) {
            enums.add(operateModeEnum);
        }
        return enums;
    }

    public static OperateModeEnum getByType(int type) {
        for (OperateModeEnum operateModeEnum : values()) {
            if (operateModeEnum.getType() == type) {
                return operateModeEnum;
            }
        }
        return null;
    }
}
