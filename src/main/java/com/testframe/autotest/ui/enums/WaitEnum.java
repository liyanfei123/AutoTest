package com.testframe.autotest.ui.enums;

public enum WaitEnum {

    Implict_Wait("implictWait", "隐式等待"),
    Presence_Element_Located("presenceOfElementLocated", "页面元素在页面中存在"),
    Element_Be_Clicked("elementToBeSelected", "页面元素处于被选中态");

    // 等待方式
    private String waitIdentity;

    private String desc;


    WaitEnum(String waitType, String desc) {
        this.waitIdentity = waitType;
        this.desc = desc;
    }


    public String getWaitIdentity() {
        return waitIdentity;
    }

    public String getDesc() {
        return desc;
    }

    public void setWaitIdentity(String waitIdentity) {
        this.waitIdentity = waitIdentity;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
