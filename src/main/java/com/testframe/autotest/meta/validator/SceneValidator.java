package com.testframe.autotest.meta.validator;

import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.meta.bo.CategoryDetailBo;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.core.enums.SceneTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


// 用于与场景相关的参数验证
@Component
@Slf4j
public class SceneValidator {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private CategoryDomain categoryDomain;

    public void validateCreate(SceneCreateCmd sceneCreateCmd) throws AutoTestException {
        try {
            checkSceneType(sceneCreateCmd);
            checkSceneTitle(sceneCreateCmd);
            checkSceneCategoryId(sceneCreateCmd);
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        }
    }

    // 验证当前场景类型是否正确
    private void checkSceneType(SceneCreateCmd createCmd) throws AutoTestException {
        if (createCmd.getType() < 1 || createCmd.getType() > 2) {
            throw new AutoTestException("测试场景类型错误");
        }
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

    private void checkSceneTitle(SceneUpdateCmd sceneUpdateCmd) {
        if (sceneDetailRepository.querySceneByTitle(sceneUpdateCmd.getTitle())) {
            throw new AutoTestException("当前测试场景标题重复");
        }
    }

    private void checkSceneCategoryId(SceneCreateCmd sceneCreateCmd) {
        if (sceneCreateCmd.getCategoryId() != null) {
            CategoryDetailBo categoryDetailBo = categoryDomain.getCategoryById(sceneCreateCmd.getCategoryId());
            if (categoryDetailBo == null) {
                throw new AutoTestException("当前类目id错误");
            }
        }
    }

    // 检验更新的场景
    public void checkSceneUpdate(SceneUpdateCmd sceneUpdateCmd) {
        checkSceneTitle(sceneUpdateCmd);
        if (!WaitModeEnum.allTypes().contains(sceneUpdateCmd.getWaitType())) {
            throw new AutoTestException("等待类型错误");
        }
        if (sceneUpdateCmd.getType() < 1 || sceneUpdateCmd.getType() > 2) {
            throw new AutoTestException("测试场景类型错误");
        }
        if (sceneDetailRepository.querySceneById(sceneUpdateCmd.getId()) == null) {
            throw new AutoTestException("当前场景不存在");
        }
        Scene scene = sceneDetailRepository.querySceneById(sceneUpdateCmd.getId());
        if (scene.getType() != sceneUpdateCmd.getType()) {
            throw new AutoTestException("不允许更新场景类型");
        }
    }

    public boolean sceneIsExist(Long sceneId) {
        Scene scene = sceneDetailRepository.querySceneById(sceneId);
        if (scene == null || scene.getIsDelete() == 1) {
            return false;
        }
        return true;
    }
}
