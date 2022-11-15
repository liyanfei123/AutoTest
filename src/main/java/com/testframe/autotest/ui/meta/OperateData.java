package com.testframe.autotest.ui.meta;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * 元素操作时的额外数据
 * @date:2022/10/26 22:07
 * @author: lyf
 */
@Data
public class OperateData {

    // 输入值，或需要判断相等的内容
    // 对于frame，需要是id
    private String value;

    // 元素属性
    private String attr;

    // 索引
    private List<Integer> indexes;

    // JS脚本文件
    private String jsExt;

    // x轴位移
    private Integer offsetX;

    // y轴位移
    private Integer offsetY;
}
