package com.testframe.autotest.core.meta.vo.common.http;

import com.testframe.autotest.core.meta.common.http.HttpStatus;
import lombok.Data;

/**
 * Description:
 *
 * @date:2021/07/14 23:10
 * @author: lyf
 */
@Data
public class HttpResult<T> {

    private int code = 200;
    private String msg;
    private T data;

    public static HttpResult error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static HttpResult error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static HttpResult error(int code, String msg) {
        HttpResult r = new HttpResult();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static HttpResult ok(String msg) {
        HttpResult r = new HttpResult();
        r.setCode(HttpStatus.SC_OK);
        r.setMsg(msg);
        return r;
    }

    public static HttpResult ok(Object data) {
        HttpResult r = new HttpResult();
        r.setCode(HttpStatus.SC_OK);
        r.setData(data);
        return r;
    }

    public static HttpResult ok() {
        HttpResult r = new HttpResult();
        r.setCode(HttpStatus.SC_OK);
        return r;
    }
}
