package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import lombok.Data;

/**
 * Description:
 * 元素操作类型-最细化
 * @date:2022/11/19 17:35
 * @author: lyf
 */
@Data
public class OperateModeInfo {

    private Integer operateMode;

    private String operateModeName;

    public static OperateModeInfo build(OperateModeEnum operateModeEnum) {
        OperateModeInfo operateModeInfo = new OperateModeInfo();
        operateModeInfo.setOperateMode(operateModeEnum.getType());
        operateModeInfo.setOperateModeName(operateModeEnum.getName());
        return operateModeInfo;
    }

}
