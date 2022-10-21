package com.testframe.autotest.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2021/07/29 16:17
 * @author: lyf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo<T> {

    private Integer code;

    private String msg;

    private String url;

    private T data;
}
