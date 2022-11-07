package com.testframe.autotest.meta.dto;


import com.testframe.autotest.meta.model.StepInfoModel;
import lombok.Data;

@Data
public class StepInfoDto {

    private Long stepId;

    private String stepName;

    private Integer stepStatus;

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

    // 验证类型
    private Integer verifyType;

    // 输入参数
    private String value;

    public static StepInfoDto build(StepInfoModel stepInfoModel) {
        StepInfoDto stepInfoDto = new StepInfoDto();
        stepInfoDto.setOperateType(stepInfoModel.getOperateType());
        stepInfoDto.setIndex(stepInfoModel.getIndex());
        stepInfoDto.setLocatorType(stepInfoModel.getLocatorType());
        stepInfoDto.setLocator(stepInfoModel.getLocator());
        stepInfoDto.setOperateMode(stepInfoModel.getOperateMode());
        stepInfoDto.setValue(stepInfoModel.getValue());
        stepInfoDto.setVerifyType(stepInfoModel.getVerifyType());
        return stepInfoDto;
    }
}
