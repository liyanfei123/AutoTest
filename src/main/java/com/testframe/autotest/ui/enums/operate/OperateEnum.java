package com.testframe.autotest.ui.enums.operate;

import java.util.List;

/**
 * 元素操作类型
 */
public enum OperateEnum {

    KEYBOARD_OPERATE(1, "keyboardOperate", "键盘操作", OperateModeEnum.keyBoard()),
    MOUSE_OPERATE(2,"mouseOperate", "鼠标操作", OperateModeEnum.mouse()),
    FRAME_OPERATE(3,"frameOperate", "frame操作", OperateModeEnum.frame()),
    BOX_OPERATE(4,"boxOperate", "单选框/复选框操作", OperateModeEnum.box()),
    POP_OPERATE(5,"popOperate", "POP弹窗操作", OperateModeEnum.pop()),
    SCRIPT_OPERATE(6, "scriptOperate", "脚本操作", OperateModeEnum.script()),
    WINDOW_OPERATE(7,"windowOperate", "窗口操作", OperateModeEnum.window());

    private int type;

    private String name;

    private String desc;

    // 元素操作子类
    private List<OperateModeEnum> operateModeEnums;

    OperateEnum(int type, String name, String desc, List<OperateModeEnum> operateModeEnums) {
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.operateModeEnums = operateModeEnums;
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

    public List<OperateModeEnum> getOperateModeEnum() {
        return operateModeEnums;
    }

}
