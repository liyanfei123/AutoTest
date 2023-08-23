package com.testframe.autotest.meta.validation.execute;


import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.core.meta.vo.common.Response;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.validation.base.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("ExecuteValidation")
public class ExecuteValidation {

    private final Validation<ExecuteChannel, ExecuteChannel> executeCreateValidation;


    @Autowired
    public ExecuteValidation(@Qualifier("executeCreateValidation") Validation<ExecuteChannel, ExecuteChannel> executeCreateValidation) {
        this.executeCreateValidation = executeCreateValidation;
    }

    public Response<ExecuteChannel> validate(ExecuteChannel executeChannel) {
        return executeCreateValidation.apply(executeChannel);
    }
}
