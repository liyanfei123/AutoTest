package com.testframe.autotest.ui.enums;

/**
 * 定位方式
 */
public enum LocatorTypeEnum {

    ById(1, "根据id字段定位"),
    ByName(2, "根据name字段定位"),
    ByClassName(3, "根据样式名称定位"),
    ByTagName(4, "根据标签名定位"),
    ByLinkText(5, "根据链接全部文字定位"),
    ByPartialLinkText(6, "根据链接部分文字定位"),
    ByCssSelector(7, "根据CSS选择器定位"),
    ByXpath(8, "根据xpath定位");

    private int type;

    private String desc;

    LocatorTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
