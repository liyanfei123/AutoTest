package com.testframe.autotest.meta.validation.scene;


import com.testframe.autotest.core.meta.vo.common.Response;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.validation.base.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("sceneValidation")
public class SceneValidation {

    private final Validation<SceneCreateCmd, SceneDetailDto> sceneCreateValidation;


    @Autowired
    public SceneValidation(@Qualifier("sceneCreateValidation") Validation<SceneCreateCmd, SceneDetailDto> sceneCreateValidation) {
        this.sceneCreateValidation = sceneCreateValidation;
    }

    public Response<SceneDetailDto> validate(SceneCreateCmd sceneCreateCmd) {
        return sceneCreateValidation.apply(sceneCreateCmd);
    }
}
