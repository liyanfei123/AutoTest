package com.testframe.autotest.ui.enums;

import io.swagger.models.auth.In;

public enum WaitEnum {

    Implict_Wait(1, "implictWait", "隐式等待"),
    Explicit_Wait(2,"explicitWait", "显式等待"),
    Presence_Element_Located(3,"presenceOfElementLocated", "页面元素在页面中存在"),
    Element_Be_Clicked(4,"elementToBeSelected", "页面元素处于被选中态");

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
