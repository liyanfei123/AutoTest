package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.Data;

/**
 * Description:
 * 元素等待最细化类
 * @date:2022/11/19 17:38
 * @author: lyf
 */
@Data
public class WaitModeInfo {

    private Integer waitMode;

    private String waitModeName;

    public static WaitModeInfo build(WaitModeEnum waitModeEnum) {
        WaitModeInfo waitModeInfo = new WaitModeInfo();
        waitModeInfo.setWaitMode(waitModeEnum.getType());
        waitModeInfo.setWaitModeName(waitModeEnum.getName());
        return waitModeInfo;
    }
}
