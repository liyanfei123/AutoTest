package com.testframe.autotest.meta.validation.base;


/**
 * 单个验证器方法
 */
public interface Validator<InputT, OutPutT> {

    OutPutT apply(InputT inputT);

}
