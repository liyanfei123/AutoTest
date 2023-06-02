package com.testframe.autotest.meta.validation.scene;

import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.Do.SceneDetailDo;
import com.testframe.autotest.core.meta.vo.common.Response;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.meta.bo.CategoryDetailBo;
import com.testframe.autotest.meta.command.SceneCreateCmd;
import com.testframe.autotest.meta.command.SceneUpdateCmd;
import com.testframe.autotest.core.enums.SceneTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// 用于与场景相关的参数验证
@Component
@Slf4j
public class SceneValidators {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneCacheService sceneCacheService;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private CategoryDomain categoryDomain;

    public Response<SceneDetailDto> validateCreateNew(SceneCreateCmd sceneCreateCmd) throws AutoTestException {
        try {
            if (sceneCreateCmd.getType() == null) {
                return Response.fail("type不允许为空!!!!");
            }
            checkSceneType(sceneCreateCmd);
            if (sceneCreateCmd.getCategoryId() == null || sceneCreateCmd.getCategoryId() == 0) {
                throw new AutoTestException("请输入正确的类目");
            }
            checkSceneCategoryId(sceneCreateCmd.getCategoryId());
            if (sceneCreateCmd.getTitle() == null) {
                throw new AutoTestException("title不允许为空");
            }
            checkSceneTitle(sceneCreateCmd.getTitle(), sceneCreateCmd.getCategoryId(), null);
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        }
        return Response.success(this.build(sceneCreateCmd));
    }

    public Response<SceneDetailDto> validateCreate(SceneCreateCmd sceneCreateCmd) throws AutoTestException {
        try {
            if (sceneCreateCmd.getType() == null) {
                throw new AutoTestException("type不允许为空");
            }
            checkSceneType(sceneCreateCmd);
            if (sceneCreateCmd.getCategoryId() == null || sceneCreateCmd.getCategoryId() == 0) {
                throw new AutoTestException("请输入正确的类目");
            }
            checkSceneCategoryId(sceneCreateCmd.getCategoryId());
            if (sceneCreateCmd.getTitle() == null) {
                throw new AutoTestException("title不允许为空");
            }
            checkSceneTitle(sceneCreateCmd.getTitle(), sceneCreateCmd.getCategoryId(), null);
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        }
        return Response.success(this.build(sceneCreateCmd));
    }

    public void validateUpdate(SceneUpdateCmd sceneUpdateCmd) throws AutoTestException {
        try {
            if (sceneUpdateCmd.getTitle() != null) {
                checkSceneTitle(sceneUpdateCmd.getTitle(), sceneUpdateCmd.getCategoryId(), sceneUpdateCmd.getId());
            }
            checkSceneCategoryId(sceneUpdateCmd.getCategoryId());
            checkWaitType(sceneUpdateCmd.getWaitType());
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

    private void checkWaitType(Integer waitType) {
        if (!WaitModeEnum.allTypes().contains(waitType)) {
            throw new AutoTestException("等待类型错误");
        }
    }
    // 验证当前标题是否重复
    public void checkSceneTitle(String title, Integer categoryId, Long nowSceneId) {
        List<SceneDetailDo> sceneDetailDos = sceneDetailRepository.querySceneByTitle(title);
        List<Long> sceneIds = sceneDetailDos.stream().map(SceneDetailDo::getSceneId).collect(Collectors.toList());
        for (Long sceneId : sceneIds) {
            if (sceneId == nowSceneId) {
                continue;
            }
            // 判断同名标题是否在一个类目下
            CategorySceneDo categorySceneDo = categorySceneRepository.queryBySceneId(sceneId);
            if (categorySceneDo.getCategoryId() == categoryId) {
                throw new AutoTestException("当前类目下测试场景标题重复");
            }
        }
    }


    private void checkSceneCategoryId(Integer categoryId) {
        CategoryDetailBo categoryDetailBo = categoryDomain.getCategoryById(categoryId);
        if (categoryDetailBo == null) {
            throw new AutoTestException("当前类目id错误");
        }
    }


    public SceneDetailDto sceneIsExist(Long sceneId) {
        SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(sceneId);
        if (sceneDetailDto == null) {
            throw new AutoTestException("当前场景id错误");
        }
        return sceneDetailDto;
    }

    public List<SceneDetailDto> sceneIsExistInCategoryId(List<Long> sceneIds, Integer categoryId) {
        List<SceneDetailDto> sceneDetailDtos = new ArrayList<>();
        for (Long sceneId : sceneIds) {
            if (sceneId == null) {
                continue;
            }
            SceneDetailDto sceneDetailDto = this.sceneIsExist(sceneId);
            if (sceneDetailDto.getCategoryId() != categoryId) {
                throw new AutoTestException("当前场景不属于该类目");
            }
            sceneDetailDtos.add(sceneDetailDto);
        }
        return sceneDetailDtos;
    }

    private SceneDetailDto build(SceneCreateCmd sceneCreateCmd) {
        SceneDetailDto sceneDetailDto = new SceneDetailDto();
        sceneDetailDto.setSceneName(sceneCreateCmd.getTitle());
        sceneDetailDto.setSceneDesc(sceneCreateCmd.getDesc());
        sceneDetailDto.setType(sceneCreateCmd.getType());
        sceneDetailDto.setCategoryId(sceneCreateCmd.getCategoryId());
        return sceneDetailDto;
    }
}
