package com.testframe.autotest.core.meta.vo.common;

import com.testframe.autotest.core.meta.vo.common.http.HttpResult;

public class Response<T> {
    public Response() {}
    private Integer code;

    private String msg;

    private T result;

    private Response<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    private Response<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    private Response<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getResult() {
        return this.result;
    }

    public static <R> Response<R> success() {
        return (new Response()).setCode(200).setMsg("成功");
    }

    public static <R> Response<R> success(R data) {
        return (new Response()).setCode(200).setMsg("成功").setResult(data);
    }

    public static <R> Response<R> fail() {
        return (new Response()).setCode(500).setMsg("失败");
    }

    public static <R> Response<R> fail(String msg) {
        return (new Response()).setCode(500).setMsg(msg);
    }

    public Boolean isSuccess() {
        return this.code.equals(200);
    }

    public Boolean isFail() {
        return !this.isSuccess();
    }

    public static <R> Response<R> of(Integer code, String msg) {
        return (new Response()).setCode(code).setMsg(msg);
    }


}
