package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.CategoryRelEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.convertor.ExeSetConverter;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.core.repository.ExeSetRepository;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.meta.command.SceneCategoryCmd;
import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.query.CategorySceneQry;
import com.testframe.autotest.meta.query.SceneQry;
import com.testframe.autotest.meta.validator.CategoryValidator;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.service.SceneCategoryService;
import com.testframe.autotest.service.SceneListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class SceneCategoryServiceImpl implements SceneCategoryService {

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Autowired
    private CategoryDomain categoryDomain;

    @Autowired
    private CategoryValidator categoryValidator;

    @Autowired
    private SceneListService sceneListService;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Autowired
    private ExeSetConverter exeSetConverter;

    @Override
    public Integer updateSceneCategory(SceneCategoryCmd sceneCategoryCmd) {
        log.info("[SceneCategoryServiceImpl:updateSceneCategory] update category-scene, sceneCategoryCmd = {}",
                JSON.toJSONString(sceneCategoryCmd));
        if (sceneCategoryCmd.getCategoryName() != null
                && sceneCategoryCmd.getCategoryName().length() > autoTestConfig.getCategoryNameLength()) {
            throw new AutoTestException("类目名称超过上限");
        }
        if (sceneCategoryCmd.getCategoryId() != null && sceneCategoryCmd.getRelatedCategoryId() != null
                && sceneCategoryCmd.getCategoryId() > 0 && sceneCategoryCmd.getRelatedCategoryId() > 0
                && sceneCategoryCmd.getCategoryId() == sceneCategoryCmd.getRelatedCategoryId()) {
            throw new AutoTestException("类目id不可与自己绑定");
        }
        categoryValidator.checkCategoryUpdate(sceneCategoryCmd);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(sceneCategoryCmd.getCategoryId());
        categoryDto.setCategoryName(sceneCategoryCmd.getCategoryName());
        categoryDto.setRelatedCategoryId(sceneCategoryCmd.getRelatedCategoryId());
        return categoryDomain.updateCategory(categoryDto);
    }

    @Override
    public Boolean deleteSceneCategory(Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            throw new AutoTestException("请输入正确的类目id");
        }
        return categoryDomain.deleteCategory(categoryId);
    }

    @Override
    public SceneListVO queryScenesByCategoryId(CategorySceneQry categorySceneQry) {
        log.info("[SceneCategoryServiceImpl:queryScenesByCategoryId] query scenes in this category, categorySceneQry = {}",
                JSON.toJSONString(categorySceneQry));
        categoryValidator.checkCategoryId(categorySceneQry.getCategoryId());
        SceneQry sceneQry = new SceneQry();
        sceneQry.setPageQry(categorySceneQry.getPageQry());
        sceneQry.setCategoryId(categorySceneQry.getCategoryId());
        SceneListVO sceneListVO = sceneListService.queryScenes(sceneQry);
        return sceneListVO;
    }

    @Override
    public List<ExeSetDto> querySetsByCategoryId(Integer categoryId) {
        log.info("[SceneCategoryServiceImpl:querySetsByCategoryId] query sets in this category, categoryId = {}",categoryId);
        categoryValidator.checkCategoryId(categoryId);
        PageQry pageQry = new PageQry();
        pageQry.setOffset(0);
        pageQry.setSize(Integer.MAX_VALUE);
        List<CategorySceneDo> categorySceneDos = categorySceneRepository.queryByCategoryId(categoryId, CategoryRelEnum.SET.getType(), pageQry);
        List<ExeSetDto> exeSetDtos = new ArrayList<>();
        categorySceneDos.forEach(categorySceneDo -> {
            ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(categorySceneDo.getSetId());
            ExeSetDto exeSetDto = exeSetConverter.DoToDto(exeSetDo);
            exeSetDto.setCategoryId(categoryId);
            exeSetDtos.add(exeSetDto);
        });
        return exeSetDtos;
    }
}
