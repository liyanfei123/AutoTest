package com.testframe.autotest.validator;

import com.testframe.autotest.command.SceneCreateCmd;
import com.testframe.autotest.command.SceneUpdateCmd;
import com.testframe.autotest.core.enums.SceneTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


// 用于与场景相关的参数验证
@Component
@Slf4j
public class SceneValidator {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    public void validateCreate(SceneCreateCmd sceneCreateCmd) {
        checkSceneType(sceneCreateCmd);
        checkSceneTitle(sceneCreateCmd);
    }

    // 验证当前场景类型是否正确
    private void checkSceneType(SceneCreateCmd createCmd) {
        if (createCmd.getType() == null || SceneTypeEnum.getByType(createCmd.getType()) == null) {
            throw new AutoTestException("场景类型错误");
        }
    }

    // 验证当前标题是否重复
    private void checkSceneTitle(SceneCreateCmd createCmd) {
        if (sceneDetailRepository.querySceneByTitle(createCmd.getTitle())) {
            throw new AutoTestException("当前测试场景标题重复");
        }
    }

    // 检验更新的场景
    public void checkSceneUpdate(SceneUpdateCmd sceneUpdateCmd) {
        if (sceneDetailRepository.querySceneById(sceneUpdateCmd.getId()) == null) {
            throw new AutoTestException("当前场景不存在");
        }
    }
}
