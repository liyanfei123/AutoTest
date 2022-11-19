package com.testframe.autotest.ui.meta;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/18 19:49
 * @author: lyf
 */
@Data
public class WaitInfo {

    Integer waitMode;

    Integer waitTime;

    public WaitInfo(Integer waitMode, Integer waitTime) {
        this.waitMode = waitMode;
        this.waitTime = waitTime;
    }

}
