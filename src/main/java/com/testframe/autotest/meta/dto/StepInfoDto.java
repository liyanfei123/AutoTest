package com.testframe.autotest.meta.dto;


import lombok.Data;

@Data
public class StepInfoDto {

    private Long stepId;

    private String stepName;

    private String stepStatus;

    // 操作类型
    private Integer operateType;

    // 元素索引
    private Integer index;

    // 定位方式
    private Integer locatorType;

    // 定位语句
    private String locator;

    // 操作方式
    private Integer operateMode;

    // 输入参数
    private String value;

}
