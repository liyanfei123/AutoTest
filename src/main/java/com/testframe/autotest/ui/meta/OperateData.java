package com.testframe.autotest.ui.meta;

import com.testframe.autotest.meta.dto.StepUIInfo;
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

    // 操作方式
    private Integer operateMode;

    // 输入值，或需要判断相等的内容
    // 对于frame，需要是id
    private String value;

    // 根据元素属性来操作
    private String attr;

    private List<Integer> indexes;

    // JS脚本文件
    private String jsExt;

    // x轴位移
    private Integer offsetX;

    // y轴位移
    private Integer offsetY;

    public static OperateData build(StepUIInfo stepUIInfo) {
        OperateData operateData = new OperateData();
        operateData.setValue(stepUIInfo.getValue());
        operateData.setIndexes(stepUIInfo.getIndexes());
        operateData.setOperateMode(stepUIInfo.getOperateMode());
        return operateData;
    }
}
