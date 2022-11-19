package com.testframe.autotest.ui.enums.wait;

import com.testframe.autotest.ui.enums.check.AssertEnum;
import com.testframe.autotest.ui.enums.check.AssertModeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 等待类型的大类
 */
public enum WaitEnum {

    IMPLICT_WAIT(1, "implictWait","隐式等待", WaitModeEnum.implict()),
    EXPLICIT_WAIT(2, "explictWait","显式等待", WaitModeEnum.explicit()),
    OTHER_WAIT(5, "otherWait","其他等待", WaitModeEnum.other());

    private int type;

    private String name;

    private String desc;

    // 元素操作子类
    private List<WaitModeEnum> waitModeEnums;

    WaitEnum(int type, String name, String desc, List<WaitModeEnum> waitModeEnums) {
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.waitModeEnums = waitModeEnums;
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

    public List<WaitModeEnum> getWaitModeEnums() {
        return waitModeEnums;
    }

    public static WaitEnum getByWaitMode(Integer mode) {
        WaitModeEnum waitModeEnum = WaitModeEnum.getByType(mode); // 最小等待类
        for (WaitEnum waitEnum : values()) {
            if (waitEnum.getWaitModeEnums().contains(waitModeEnum)) {
                return waitEnum;
            }
        }
        return null;
    }

    public static List<WaitEnum> getTypes() {
        List<WaitEnum> allTypes = new ArrayList<>();
        for (WaitEnum waitEnum : values()) {
            allTypes.add(waitEnum);
        }
        return allTypes;
    }

}
