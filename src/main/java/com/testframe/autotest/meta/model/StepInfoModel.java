package com.testframe.autotest.meta.model;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/10/21 22:41
 * @author: lyf
 */
@Data
public class StepInfoModel {

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

    // 验证方式
    private Integer verifyType;

    // 输入参数
    private String value;

}
