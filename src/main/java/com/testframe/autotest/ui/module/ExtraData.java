package com.testframe.autotest.ui.module;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/10/26 22:07
 * @author: lyf
 */
@Data
public class ExtraData {

    // 输入值，或需要判断相等的内容
    private String value;

    // 元素属性
    private String attr;

    // 索引
    private Integer index;

}
