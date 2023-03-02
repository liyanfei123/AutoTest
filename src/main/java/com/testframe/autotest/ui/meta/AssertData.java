package com.testframe.autotest.ui.meta;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/10/27 21:19
 * @author: lyf
 */
@Data
public class AssertData {

    // 验证方式
    private Integer assertMode;

    // 预期的文本
    private String expectString;

    public static AssertData build(StepUIInfo stepUIInfo) {
        AssertData checkData = new AssertData();
        checkData.setExpectString(stepUIInfo.getValue());
        checkData.setAssertMode(stepUIInfo.getAssertMode());
        return checkData;
    }

}
