package com.testframe.autotest.core.exception;

/**
 * Description:
 *
 * @date:2022/10/26 21:54
 * @author: lyf
 */
public class ActionExpection extends RuntimeException {

    public ActionExpection() {}

    public ActionExpection(String msg) {
        super(msg);
    }

}

