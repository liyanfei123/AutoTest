package com.testframe.autotest.ui.enums.wait;

import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WaitEnum {

    NO_WAIT(0, "noWait", "无需等待"),
    SLEEP_WAIT(1, "睡眠", "直接等待几秒"),
    Implict_Wait(2, "implictWait", "隐式等待"),
    Explicit_Wait(3,"explicitWait", "显式等待"),
    Presence_Element_Located(4,"presenceOfElementLocated", "页面元素在页面中存在"),
    Element_Be_Clicked(5,"elementToBeSelected", "页面元素处于被选中态");

    private int type;
    // 等待方式
    private String waitIdentity;

    private String desc;


    WaitEnum(int type, String waitType, String desc) {
        this.type = type;
        this.waitIdentity = waitType;
        this.desc = desc;
    }

    public static WaitEnum getByType(int type) {
        for (WaitEnum waitEnum: values()) {
            if (waitEnum.getType() == type) {
                return waitEnum;
            }
        }
        return null;
    }

    public static List<Integer> allTypes() {
        List<Integer> types = new ArrayList<>();
        for (WaitEnum waitEnum: values()) {
            types.add(waitEnum.getType());
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
}
