package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.OperateTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * 元素等待大类
 * @date:2022/11/19 17:39
 * @author: lyf
 */
@Data
public class WaitClassInfo {

    private Integer classType;

    private String waitClassName;

    private List<WaitModeInfo> waitModeInfos;

}
