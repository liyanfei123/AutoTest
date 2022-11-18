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

    Integer waitType;

    Integer waitTime;

    public WaitInfo(Integer waitType, Integer waitTime) {
        this.waitType = waitType;
        this.waitTime = waitTime;
    }

}
