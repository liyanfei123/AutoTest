package com.testframe.autotest.ui.enums.wait;

import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WaitModeEnum {
    // 隐式等待
    Implict_Wait(10, "ImplictWait", "隐式等待", "隐式等待"),

    // 显式等待
    Presence_Element_Located(20,"ExplicitPresenceWait", "元素存在等待", "页面元素在页面中存在"),
    Element_Be_Clicked(21,"elementToBeSelected", "元素选中等待", "页面元素处于被选中态"),

    // 其他等待
    NO_WAIT(50, "NoWait", "无需等待", "无需等待"),
    SLEEP_WAIT(51, "ThreadSleepWait", "睡眠", "直接等待几秒");

    private int type;

    // 等待方式 和类名相同
    private String waitIdentity;

    private String name;

    private String desc;


    WaitModeEnum(int type, String waitIdentity, String name, String desc) {
        this.type = type;
        this.waitIdentity = waitIdentity;
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public static WaitModeEnum getByType(int type) {
        for (WaitModeEnum waitModeEnum: values()) {
            if (waitModeEnum.getType() == type) {
                return waitModeEnum;
            }
        }
        return null;
    }

    public static List<Integer> allTypes() {
        List<Integer> types = new ArrayList<>();
        for (WaitModeEnum waitModeEnum: values()) {
            types.add(waitModeEnum.getType());
        }
        return types;
    }

    public int getType() {
        return type;
    }

    public String getWaitIdentity() {
        return waitIdentity;
    }

    public String getDesc() {
        return desc;
    }

    public void setType(int type) {
        this.type = type;
    }
    public void setWaitIdentity(String waitIdentity) {
        this.waitIdentity = waitIdentity;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static List<WaitModeEnum> implict() {
        return Arrays.asList(Implict_Wait);
    }

    public static List<WaitModeEnum> explicit() {
        return Arrays.asList(Presence_Element_Located, Element_Be_Clicked);
    }

    public static List<WaitModeEnum> other() {
        return Arrays.asList(NO_WAIT, SLEEP_WAIT);
    }
}
