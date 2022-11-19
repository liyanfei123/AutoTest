package com.testframe.autotest.core.exception;

/**
 * Description:
 * selenium执行时所抛出的异常
 * @date:2022/11/19 12:03
 * @author: lyf
 */
public class SeleniumRunException extends RuntimeException {

    public SeleniumRunException() {}

    public SeleniumRunException(String msg) {
        super(msg);
    }

}
