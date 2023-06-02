package com.testframe.autotest.meta.validation.base;

import com.testframe.autotest.core.meta.vo.common.Response;
import io.swagger.models.auth.In;

/**
 * 验证的过程或行为
 */
public interface Validation<InputT, OutPutT> {

    /**
     * validator验证方法
     * @param inputT
     * @return
     */
    Response<OutPutT> apply(InputT inputT);

    /**
     * 追加验证
     * @param after 使用上次的验证输出当作追加验证的输入
     * @return
     * @param <V> 追加验证的返回数据
     */
    <V> Validation<InputT, V> addThen(Validator<OutPutT, Response<V>> after);
}
