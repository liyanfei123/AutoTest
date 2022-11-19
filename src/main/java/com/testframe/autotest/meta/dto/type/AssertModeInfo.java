package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/19 17:41
 * @author: lyf
 */
@Data
public class AssertModeInfo {

    private Integer assertMode;

    private String assertModeName;

    public static AssertModeInfo build(AssertModeEnum assertModeEnum) {
        AssertModeInfo assertModeInfo = new AssertModeInfo();
        assertModeInfo.setAssertMode(assertModeEnum.getType());
        assertModeInfo.setAssertModeName(assertModeEnum.getName());
        return assertModeInfo;
    }
}
