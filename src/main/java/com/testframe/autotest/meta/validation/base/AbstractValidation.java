package com.testframe.autotest.meta.validation.base;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.meta.vo.common.Response;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;


/**
 * 在抽象类实现接口时，一般来说需要实现接口中的所有方法。
 * 但是，如果该抽象类本身也是一个抽象类，则可以不必实现接口中的所有方法，而是将这些方法留给继承该抽象类的具体子类来实现。
 * 需要注意的是，如果一个类实现了一个接口，那么该类必须实现接口中的所有方法，否则该类必须声明为抽象类。
 * 在Java中，如果一个类实现了一个接口，但没有实现接口中的所有方法，则编译器会报错。
 * @param <InputT>
 * @param <OutputT>
 */
@Slf4j
public abstract class AbstractValidation<InputT, OutputT> implements Validation<InputT, OutputT> {

    private AbstractValidation() {}


    @Override
    public <V> Validation<InputT, V> addThen(Validator<OutputT, Response<V>> after) {
        Validation<InputT, OutputT> current = this;
        return new AbstractValidation<InputT, V>() {
            @Override
            public Response<V> apply(InputT inputT) {
                Response<OutputT> currentResponse = current.apply(inputT);
                if (currentResponse.isSuccess()) {
                    OutputT currentResponseOutputT = currentResponse.getResult();
                    return after.apply(currentResponseOutputT);
                } else {
                    return Response.of(currentResponse.getCode(), currentResponse.getMsg());
                }
            }
        };
    }

    /**
     * 初始化验证器方法
     * @return
     * @param <InputT>
     */
    public static <InputT> Validation<InputT, InputT> init() {
        return from(start());
    }

    public static <InputT, OutputT> AbstractValidation<InputT, OutputT> from(Validator<InputT, Response<OutputT>> validator) {
        // 实例，需要实现未实现的接口方法
        return new AbstractValidation<InputT, OutputT>() {
            @Override
            public Response<OutputT> apply(InputT inputT) {
                return validator.apply(inputT);
            }
        };
    }


    public static <InputT> Validator<InputT, Response<InputT>> start() {
        return Response::success;
    }

}
