package com.testframe.autotest.ui.elements.module.check;

import com.testframe.autotest.meta.dto.StepUIInfo;
import lombok.Data;

/**
 * Description:
 *
 * @date:2022/10/27 21:19
 * @author: lyf
 */
@Data
public class CheckData {

    // 预期的文本
    private String expectString;

    public static CheckData build(StepUIInfo stepUIInfo) {
        CheckData checkData = new CheckData();
        checkData.setExpectString(stepUIInfo.getValue());
        return checkData;
    }

}
