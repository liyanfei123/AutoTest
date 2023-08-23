package com.testframe.autotest.meta.validation.execute;

import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.meta.validation.base.AbstractValidation;
import com.testframe.autotest.meta.validation.base.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecuteValidationConfig {

    private final ExecuteValidators executeValidators;

    @Autowired
    public ExecuteValidationConfig(ExecuteValidators executeValidators) {
        this.executeValidators = executeValidators;
    }


    @Bean("executeCreateValidation")
    public Validation<ExecuteChannel, ExecuteChannel> executeValidation() {
        return AbstractValidation.<ExecuteChannel>init()
                .addThen(executeValidators::validateSet)
                .addThen(executeValidators::validateScene);
    }
}
