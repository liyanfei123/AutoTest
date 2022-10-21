package com.testframe.autotest.core.exception;


import com.testframe.autotest.core.meta.common.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @date:2021/07/29 16:36
 * @author: lyf
 */
@ControllerAdvice(basePackages = {"com.team"})
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler({AutoTestException.class})
    @ResponseBody
    public ErrorInfo<Object> defaultErrorhandler(HttpServletRequest request, Exception e) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        errorInfo.setMsg(e.getMessage());
        errorInfo.setUrl(request.getRequestURI());
        return errorInfo;
    }
}
