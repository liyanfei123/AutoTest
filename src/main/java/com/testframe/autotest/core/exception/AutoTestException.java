package com.testframe.autotest.core.exception;

import lombok.Data;

@Data
public class AutoTestException extends RuntimeException {

    private Integer errCode;

    public AutoTestException() {}

    public AutoTestException(String msg) {
        super(msg);
    }

    public AutoTestException(Integer errorCode, String msg) {
        super(msg);
        this.errCode = errorCode;
    }

}
