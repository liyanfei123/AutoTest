package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.OperateTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * 元素操作大类
 * @date:2022/11/19 17:37
 * @author: lyf
 */
@Data
public class OperateClassInfo {

    private Integer classType;

    private String OperateClassName;

    private List<OperateModeInfo> operateModeInfos;
}
