package com.testframe.autotest.job.base;


import com.testframe.autotest.core.meta.vo.common.Response;

public abstract class ITaskHandler {

    public ITaskHandler() {
    }

    public abstract Response<String> execute() throws Exception;

}
