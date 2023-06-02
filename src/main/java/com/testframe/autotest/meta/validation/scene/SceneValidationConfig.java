package com.testframe.autotest.meta.validation.scene;


import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.validation.base.AbstractValidation;
import com.testframe.autotest.meta.validation.base.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SceneValidationConfig {

    private final SceneValidators sceneValidators;

    /**
     * SceneValidationConfig(SceneValidators sceneValidators)是在创建SceneValidationConfig实例时注入的。在
     * 该类中使用@Autowired注解标注了构造函数，Spring在创建SceneValidationConfig实例时会自动将SceneValidators对象注入到构造函数中，然后实例化SceneValidationConfig对象。
     * 这个过程称为依赖注入（Dependency Injection）
     * 当其他类需要使用SceneValidationConfig实例时，Spring会返回已经实例化并注入所需依赖的SceneValidationConfig对象。
     * @param sceneValidators
     */
    @Autowired
    public SceneValidationConfig(SceneValidators sceneValidators) {
        this.sceneValidators = sceneValidators;
    }

    @Bean("sceneCreateValidation")
    public Validation<SceneCreateCmd, SceneDetailDto> sceneCreateValidation() {
        return AbstractValidation.<SceneCreateCmd>init()
                .addThen(sceneValidators::validateCreateNew);
    }
}
