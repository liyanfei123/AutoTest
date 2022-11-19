package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.OperateTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * 元素验证大类
 * @date:2022/11/19 17:40
 * @author: lyf
 */
@Data
public class AssertClassInfo {

    private Integer classType;

    private String assertClassName;

    private List<AssertModeInfo> assertModeInfos;

}
