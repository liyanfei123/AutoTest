package com.testframe.autotest.meta.dto;

import com.testframe.autotest.meta.model.StepInfoModel;
import com.testframe.autotest.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.testframe.autotest.util.StringUtils.strToList;

// 步骤中关于ui执行信息的参数
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepUIInfo {

    // 操作类型
    private Integer operateType;

    // 元素索引
    private List<Integer> indexes;

    // 定位方式
    private Integer locatorType;

    // 定位语句
    private String locator;

    // 操作方式
    private Integer operateMode;

    // 验证类型
    private Integer assertMode;

    // 等待类型
    private Integer waitMode;

    // 输入参数
    private String value;

    public static StepUIInfo build(StepInfoModel stepInfoModel) {
        StepUIInfo stepUIInfo = new StepUIInfo();
        stepUIInfo.setOperateType(stepInfoModel.getOperateType());
        stepUIInfo.setIndexes(strToList(stepInfoModel.getIndexes()));
        stepUIInfo.setLocatorType(stepInfoModel.getLocatorType());
        stepUIInfo.setLocator(stepInfoModel.getLocator());
        stepUIInfo.setOperateMode(stepInfoModel.getOperateMode());
        stepUIInfo.setValue(stepInfoModel.getValue());
        stepUIInfo.setAssertMode(stepInfoModel.getAssertMode());
        stepUIInfo.setWaitMode(stepInfoModel.getWaitMode());
        return stepUIInfo;
    }

}
